package co.kr.sikim.suinproject.service;

import co.kr.sikim.suinproject.domain.User;

public interface AuthService {
    // OAuth2 로그인 성공 시: email로 사용자 upsert
    // 신규: 기본 책장 생성 후 사용자 정보 반환
    User upsertOAuthUser(String email, String name);
}
