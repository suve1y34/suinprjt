package co.kr.sikim.suinproject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(maxTotal);
        cm.setDefaultMaxPerRoute(defaultMaxPerRoute);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectTimeoutMs)
                .setResponseTimeout(readTimeoutMs)
                .setConnectionRequestTimeout(connectionRequestTimeoutMs)
                .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(requestConfig)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
    }
}
