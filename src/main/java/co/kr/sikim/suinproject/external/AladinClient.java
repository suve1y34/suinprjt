package co.kr.sikim.suinproject.external;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

    @Value("${aladin.query-type:Title}")
    private String queryType;

    @Value("${aladin.output:js}")
    private String output;

    public String searchRaw(String keyword, int start, int maxResults) {
        // Aladin API
        // 주요 파라미터: TTBKey, Query, QueryType, SearchTarget, Start, MaxResults, output=js
        String url = baseUrl
                + "?ttbkey=" + enc(ttbKey)
                + "&Query=" + enc(keyword)
                + "&QueryType=" + enc(queryType)
                + "&SearchTarget=" + enc(searchTarget)
                + "&start=" + start
                + "&MaxResults=" + maxResults
                + "&output=" + enc(output);

        return restTemplate.getForObject(url, String.class);
    }

    private String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}
