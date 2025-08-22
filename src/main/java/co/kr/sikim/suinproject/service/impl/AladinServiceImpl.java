package co.kr.sikim.suinproject.service.impl;

import co.kr.sikim.suinproject.domain.AladinCache;
import co.kr.sikim.suinproject.dto.aladin.AladinBookResponse;
import co.kr.sikim.suinproject.dto.aladin.AladinSearchRequest;
import co.kr.sikim.suinproject.external.AladinClient;
import co.kr.sikim.suinproject.external.AladinParser;
import co.kr.sikim.suinproject.mapper.AladinCacheMapper;
import co.kr.sikim.suinproject.service.AladinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AladinServiceImpl implements AladinService {
    private final AladinClient client;
    private final AladinParser parser;
    private final AladinCacheMapper cMapper;

    @Override
    public List<AladinBookResponse> searchBooks(AladinSearchRequest req) {
        // 1) 호출
        String raw;
        try {
            raw = client.searchRaw(req.getKeyword(), nvl(req.getStart(), 1), nvl(req.getMaxResults(), 20));
        } catch (Exception e) {
            log.warn("Aladin upstream error: {}", e.getMessage());
            // 실패
            return Collections.emptyList();
        }

        // 2) 파싱
        List<AladinBookResponse> list = parser.parseSearch(raw);

        // 3) 캐시 upsert
        for (AladinBookResponse b : list) {
            if (b.getIsbn13Code() == null || b.getIsbn13Code().isBlank()) continue;
            boolean exists = cMapper.existAladinCacheByIsbn13Code(b.getIsbn13Code());
            AladinCache c = new AladinCache();
            c.setIsbn13Code(b.getIsbn13Code());
            c.setRawJson(raw);
            if (exists) {
                cMapper.updateAladinCache(c);
                log.debug("[ALADIN][CACHE-HIT] isbn13={}", b.getIsbn13Code());
            } else {
                cMapper.insertAladinCache(c);
                log.debug("[ALADIN][CACHE-MISS] isbn13={}", b.getIsbn13Code());
            }
        }

        return list;
    }

    private int nvl(Integer v, int d) { return v == null ? d : v; }
}
