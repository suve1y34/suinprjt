package co.kr.sikim.suinproject.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class NicknameGenerator {
    public enum Strategy { HASHED, ALLITERATION, NO_NUMBER }

    private static final SecureRandom RND = new SecureRandom();

    // 형용사(모두 '한'으로 끝나도록)
    private static final String[] ADJ = {
            "행복한","평온한","차분한","용감한","상냥한","든든한","건강한","소중한",
            "영리한","정직한","유쾌한","진지한","명랑한","기특한","화목한","관대한",
            "감사한","겸손한","성실한","솔직한","현명한","따뜻한","단단한","선한"
    };

    // 명사
    private static final String[] NOUN = {
            "여우","토끼","다람쥐","사자","호랑이","곰","사슴","펭귄","고래","하마",
            "나비","고양이","강아지","새","별","달","해","별똥별","바다","파도",
            "나무","꽃","산","강","노을","바람","눈송이","돌","행성","은하","숲"
    };

    // (선택) 금칙어
    private static final Set<String> BAD = Set.of(
            // 금칙어 필요 시 추가
    );

    private NicknameGenerator() {}

    // ===== 공개 메서드 =====

    /**
     * 계정 식별자(providerId 또는 email)을 시드로 받아 유니크 닉네임 생성
     * existsCheck: 이미 존재하면 true를 반환하는 람다 (ex) name -> userMapper.existsUserByNickname(name)
     */
    public static String generateUnique(String seed, Strategy strategy, java.util.function.Predicate<String> existsCheck) {
        // 기본 후보 뽑기
        String base = switch (strategy) {
            case HASHED -> hashedPair(seed);
            case ALLITERATION -> alliterationPair(seed);
            case NO_NUMBER -> simpleRandom(); // 베이스만 필요
        };
        base = sanitize(base);

        // 1차: 베이스 그대로 시도
        if (isOk(base) && !existsCheck.test(base)) return base;

        // 충돌 해결
        return switch (strategy) {
            case HASHED -> solveByShortTail(base, seed, existsCheck);
            case ALLITERATION -> solveByAlliteration(base, seed, existsCheck);
            case NO_NUMBER -> solveByDoubleNoun(base, seed, existsCheck);
        };
    }

    // ===== 전략 구현 =====

    // 전략 A: 결정형 해시 — 같은 시드면 같은 결과
    private static String hashedPair(String seed) {
        int a = idxFromHash(seed, "A");
        int b = idxFromHash(seed, "B");
        return ADJ[a] + " " + NOUN[b];
    }

    // 전략 B: 두운(초성 일치)
    private static String alliterationPair(String seed) {
        int ni = idxFromHash(seed, "N") % NOUN.length;
        String noun = NOUN[ni];
        char ic = initialConsonant(noun);
        List<String> pool = adjByInitial(ic);
        if (pool.isEmpty()) { // 없으면 일반 해시
            return hashedPair(seed);
        }
        int ai = idxFromHash(seed, "A2") % pool.size();
        return pool.get(ai) + " " + noun;
    }

    // 전략 C: 숫자 없이 — 기본 랜덤 베이스
    private static String simpleRandom() {
        return ADJ[RND.nextInt(ADJ.length)] + " " + NOUN[RND.nextInt(NOUN.length)];
    }

    // 해시 기반 짧은 꼬리 (base36 2자리)
    private static String solveByShortTail(String base, String seed, java.util.function.Predicate<String> existsCheck) {
        for (int i = 1; i <= 50; i++) {
            String sfx = shortBase36(seed + "#" + i);
            String cand = base + "-" + sfx;
            if (isOk(cand) && !existsCheck.test(cand)) return cand;
        }
        // 최후 수단
        return base + "-" + UUID.randomUUID().toString().substring(0, 6);
    }

    // 두운 전략: 같은 초성으로 재조합 시도 → 마지막에 짧은 꼬리
    private static String solveByAlliteration(String base, String seed, java.util.function.Predicate<String> existsCheck) {
        String noun = base.split(" ")[1];
        char ic = initialConsonant(noun);
        List<String> pool = adjByInitial(ic);
        // 다른 형용사로 몇 번 더
        for (int i = 0; i < Math.min(20, pool.size()); i++) {
            String cand = pool.get((idxFromHash(seed, "R"+i) + i) % pool.size()) + " " + noun;
            cand = sanitize(cand);
            if (isOk(cand) && !existsCheck.test(cand)) return cand;
        }
        return solveByShortTail(base, seed, existsCheck);
    }

    // 숫자 없이: 명사 2개 합성으로 회피 (예: "행복한 여우숲")
    private static String solveByDoubleNoun(String base, String seed, java.util.function.Predicate<String> existsCheck) {
        String[] sp = base.split(" ");
        String adj = sp[0], noun = sp.length > 1 ? sp[1] : NOUN[0];
        for (int i = 0; i < 30; i++) {
            String noun2 = NOUN[(idxFromHash(seed, "N2"+i) + i) % NOUN.length];
            if (noun2.equals(noun)) continue;
            String cand = adj + " " + (noun + noun2); // 공백 그대로 두고 UI에서 한 칸 유지
            cand = sanitize(cand);
            if (isOk(cand) && !existsCheck.test(cand)) return cand;
        }
        // 정말 안 되면 짧은 꼬리
        return solveByShortTail(base, seed, existsCheck);
    }

    // ===== 보조 함수 =====

    private static int idxFromHash(String seed, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] h = md.digest((seed + "|" + salt).getBytes(StandardCharsets.UTF_8));
            // 양수 인덱스
            int v = ((h[0] & 0xff) << 16) | ((h[1] & 0xff) << 8) | (h[2] & 0xff);
            return Math.abs(v);
        } catch (Exception e) {
            return Math.abs(seed.hashCode());
        }
    }

    private static String shortBase36(String s) {
        long v = Math.abs(idxFromHash(s, "T") + 1L);
        String b36 = Long.toString(v, 36);
        return b36.length() > 2 ? b36.substring(0, 2) : String.format("%2s", b36).replace(' ', '0');
    }

    // 한글 초성 추출
    private static char initialConsonant(String word) {
        if (word == null || word.isEmpty()) return 0;
        char ch = word.charAt(0);
        if (ch < 0xAC00 || ch > 0xD7A3) return 0;
        final char[] CHO = {'ㄱ','ㄲ','ㄴ','ㄷ','ㄸ','ㄹ','ㅁ','ㅂ','ㅃ','ㅅ','ㅆ','ㅇ','ㅈ','ㅉ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ'};
        int choIdx = (ch - 0xAC00) / (21 * 28);
        return CHO[choIdx];
    }

    private static List<String> adjByInitial(char ic) {
        if (ic == 0) return List.of();
        List<String> out = new ArrayList<>();
        for (String a : ADJ) {
            if (initialConsonant(a) == ic) out.add(a);
        }
        return out;
    }

    private static boolean isOk(String s) {
        if (s == null) return false;
        String t = s.replaceAll("\\s+", "");
        if (t.length() > 12) return false; // 길이 제한(원하면 조정)
        for (String bad : BAD) if (t.contains(bad)) return false;
        return true;
    }

    private static String sanitize(String s) {
        return s.replaceAll("\\s+", " ").trim();
    }
}
