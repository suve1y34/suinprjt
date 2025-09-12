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
                .queryParam("Query",  keyword)
                .queryParam("QueryType", queryType)  // Keyword / Title / Author
                .queryParam("SearchTarget", searchTarget) // Book
                .queryParam("start", start)
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

    public String extractCoverUrlFromLookup(String rawJson) {
        try {
            JsonNode root = om.readTree(rawJson);
            JsonNode items = root.path("item");
            if (items.isArray() && items.size() > 0) {
                JsonNode it = items.get(0);
                String large = readText(it.path("coverLargeUrl"));
                if (isNotBlank(large)) return large;

                String cover = readText(it.path("cover"));
                if (isNotBlank(cover)) return cover;

                String small = readText(it.path("coverSmallUrl"));
                if (isNotBlank(small)) return small;

                // 일부 응답은 subInfo 하위에 있을 수 있음 (희귀)
                JsonNode sub = it.path("subInfo");
                if (sub.isObject()) {
                    String sLarge = readText(sub.path("coverLargeUrl"));
                    if (isNotBlank(sLarge)) return sLarge;
                    String sCover = readText(sub.path("cover"));
                    if (isNotBlank(sCover)) return sCover;
                    String sSmall = readText(sub.path("coverSmallUrl"));
                    if (isNotBlank(sSmall)) return sSmall;
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

    private String readText(JsonNode n) {
        if (n == null || n.isNull()) return null;
        if (n.isTextual()) return n.asText();
        return n.toString();
    }

    private boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}
