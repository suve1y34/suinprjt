package co.kr.sikim.suinproject.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate: HttpClient5 기반 커넥션 풀 & 타임아웃 적용
 */
@Configuration
public class RestClientConfig {
    @Bean
    public RestTemplate restTemplate(
            @Value("${httpclient.connect-timeout-ms:3000}") int connectTimeoutMs,
            @Value("${httpclient.read-timeout-ms:5000}") int readTimeoutMs,
            @Value("${httpclient.connection-request-timeout-ms:3000}") int connectionRequestTimeoutMs,
            @Value("${httpclient.pool.max-total:100}") int maxTotal,
            @Value("${httpclient.pool.default-max-per-route:20}") int defaultMaxPerRoute
    ) {
        // 1) 커넥션 풀
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(maxTotal);
        cm.setDefaultMaxPerRoute(defaultMaxPerRoute);

        // 2) 타임아웃 (HttpClient5는 Timeout 타입 필수)
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(connectTimeoutMs))
                .setResponseTimeout(Timeout.ofMilliseconds(readTimeoutMs))
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(connectionRequestTimeoutMs))
                .build();

        // 3) HttpClient
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(requestConfig)
                .evictExpiredConnections()                          // 만료 연결 제거
                .evictIdleConnections(Timeout.ofSeconds(30))        // 유휴 연결 정리
                .build();

        // 4) RestTemplate (HttpClient5용 팩토리)
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
    }
}
