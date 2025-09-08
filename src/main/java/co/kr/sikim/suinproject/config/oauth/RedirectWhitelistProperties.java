package co.kr.sikim.suinproject.config.oauth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.security")
@Getter
@Setter
public class RedirectWhitelistProperties {
    /** 허용 리다이렉트 URI 목록 (FE 도메인) */
    private List<String> redirectWhitelist = List.of("http://localhost:9217");

    public boolean isAllowed(String uri) {
        try {
            URI u = URI.create(uri);
            String norm = u.getScheme() + "://" + u.getHost() + (u.getPort() > 0 ? ":" + u.getPort() : "");
            return redirectWhitelist.stream().anyMatch(allowed -> {
                try {
                    URI a = URI.create(allowed);
                    String an = a.getScheme() + "://" + a.getHost() + (a.getPort() > 0 ? ":" + a.getPort() : "");
                    return an.equalsIgnoreCase(norm);
                } catch (Exception e) { return false; }
            });
        } catch (Exception e) { return false; }
    }
}
