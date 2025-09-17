package co.kr.sikim.suinproject.config.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final RedirectWhitelistProperties whitelist;

    private String fallbackRedirect() {
        // 해시 라우트로 콜백 (해시 필수)
        return whitelist.getRedirectWhitelist().stream()
                .findFirst()
                .map(base -> base + "/#/login/callback")
                .orElse("http://ckk122.cafe24.com/#/login/callback");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException ex) throws IOException {
        String requested = request.getParameter("redirect_uri");
        String redirect = (requested != null && whitelist.isAllowed(requested))
                ? requested
                : fallbackRedirect();

        // ★ 해시를 인식해서 error 파라미터를 "해시 뒤"에 붙인다
        String target = appendParamRespectingHash(redirect, "error", "oauth2_failure");

        getRedirectStrategy().sendRedirect(request, response, target);
    }

    /**
     * redirect가 해시(#)를 포함하면, 파라미터를 해시 뒤의 fragment 쿼리에 붙인다.
     * 예) http://host/#/login/callback  → http://host/#/login/callback?key=val
     *    http://host/#/login/callback?x=1 → http://host/#/login/callback?x=1&key=val
     * 해시가 없으면 일반 쿼리스트링으로 붙인다.
     */
    private static String appendParamRespectingHash(String redirect, String key, String value) {
        String encKey = URLEncoder.encode(key, StandardCharsets.UTF_8);
        String encVal = URLEncoder.encode(value, StandardCharsets.UTF_8);

        int hash = redirect.indexOf('#');
        if (hash >= 0) {
            String base = redirect.substring(0, hash);
            String fragment = redirect.substring(hash + 1); // e.g. "/login/callback" or "/login/callback?x=1"
            String sep = (fragment.contains("?") ? "&" : "?");
            return base + "#" + fragment + sep + encKey + "=" + encVal;
        } else {
            // 해시가 없으면 일반 쿼리스트링
            String sep = (redirect.contains("?") ? "&" : "?");
            return redirect + sep + encKey + "=" + encVal;
        }
    }
}