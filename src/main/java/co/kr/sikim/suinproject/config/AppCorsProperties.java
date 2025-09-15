package co.kr.sikim.suinproject.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CORS 허용 오리진 목록 프로퍼티
 * fe 도메인만 허용
 * */
@Component
@ConfigurationProperties(prefix = "app.cors")
@Getter
@Setter
public class AppCorsProperties {
    private List<String> allowedOrigins = List.of("http://localhost:9217");
}
