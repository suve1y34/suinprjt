package co.kr.sikim.suinproject.external;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class AladinClient {
    private final RestTemplate restTemplate;

    @Value("${aladin.base-url}")
    private String baseUrl;

    @Value("${aladin.ttb-key}")
    private String ttbKey;

    @Value("${aladin.search-target:Book}")
    private String searchTarget;

    @Value("${aladin.query-type:Keyword}")
    private String queryType;

    @Value("${aladin.output:js}")
    private String output;

    public String searchRaw(String keyword, int start, int maxResults) {
        // Aladin API
        // 주요 파라미터: TTBKey, Query, QueryType, SearchTarget, Start, MaxResults, output=js
        URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("ttbkey", ttbKey)
                .queryParam("Query",  keyword)       // ✅ UriComponentsBuilder가 UTF-8로 정확히 인코딩
                .queryParam("QueryType", queryType)  // Keyword / Title / Author
                .queryParam("SearchTarget", searchTarget) // Book
                .queryParam("start", start)          // ✅ 대문자
                .queryParam("MaxResults", maxResults)
                .queryParam("output", output)        // js 또는 xml (대문자 O)
                .queryParam("Version", "20131101")
                .build(false)
                .encode(StandardCharsets.UTF_8)
                .toUri();

        return restTemplate.getForObject(uri, String.class);
    }

    private String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}
