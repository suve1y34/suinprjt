package co.kr.sikim.suinproject.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper om = new ObjectMapper();

    @Value("${aladin.base-url}")
    private String baseUrl;

    @Value("${aladin.lookup-url}")
    private String lookupUrl;

    @Value("${aladin.ttb-key}")
    private String ttbKey;

    @Value("${aladin.search-target:Book}")
    private String searchTarget;

    @Value("${aladin.query-type:Keyword}")
    private String queryType;

    @Value("${aladin.output:js}")
    private String output;

    // 검색
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
    // 상세 - ISBN13 기준
    public String lookupRawByIsbn13(String isbn13) {
        URI uri = UriComponentsBuilder.fromHttpUrl(lookupUrl)
                .queryParam("ttbkey", ttbKey)
                .queryParam("itemIdType", "ISBN13")
                .queryParam("ItemId", isbn13)
                .queryParam("Output", "JS")
                .queryParam("Version", "20131101")
                .build(false)
                .encode(StandardCharsets.UTF_8)
                .toUri();
        return restTemplate.getForObject(uri, String.class);
    }

    // LookUp 응답에서 itemPage 추출 (정수/문자)
    public Integer extractItemPageFromLookup(String rawJson) {
        try {
            JsonNode root = om.readTree(rawJson);
            JsonNode items = root.path("item");
            if (items.isArray() && items.size() > 0) {
                JsonNode it = items.get(0);
                Integer p = readInt(it.path("itemPage"));
                if (p != null) return p;
                JsonNode subInfo = it.path("subInfo");
                if (subInfo.isObject()) {
                    p = readInt(subInfo.path("itemPage"));
                    if (p != null) return p;
                }
            }
        } catch (Exception ignore) {}
        return null;
    }

    private Integer readInt(JsonNode n) {
        if (n == null || n.isNull()) return null;
        if (n.isInt()) return n.asInt();
        if (n.isTextual()) {
            try { return Integer.parseInt(n.asText().trim()); } catch (Exception ignore) {}
        }
        return null;
    }

    private String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}
