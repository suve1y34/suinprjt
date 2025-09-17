package co.kr.sikim.suinproject.config.oauth;

import co.kr.sikim.suinproject.config.JwtTokenProvider;
import co.kr.sikim.suinproject.domain.User;
import co.kr.sikim.suinproject.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final AuthService authSer;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedirectWhitelistProperties whitelist;

    // 해시 라우트 기준 콜백(기본값)
    private String fallbackRedirect() {
        return whitelist.getRedirectWhitelist().stream()
                .findFirst()
                .map(base -> base + "/#/login/callback")
                .orElse("http://ckk122.cafe24.com/#/login/callback");
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String requested = request.getParameter("redirect_uri");
        String redirect = (requested != null && whitelist.isAllowed(requested))
                ? requested
                : fallbackRedirect(); // 반드시 해시 포함 콜백

        String email = null;
        String name  = null;

        Object principal = authentication.getPrincipal();
        if (principal instanceof OidcUser oidc) {
            email = oidc.getEmail();
            String fullName  = oidc.getFullName();
            String nameAttr  = oidc.getAttribute("name");
            String givenName = oidc.getGivenName();
            name = firstNonBlank(fullName, nameAttr, givenName, email, "");
        } else if (principal instanceof OAuth2User ou) {
            @SuppressWarnings("unchecked")
            Map<String, Object> unified = (Map<String, Object>) ou.getAttributes().get("unified");
            if (unified != null) {
                email = toStr(unified.get("email"));
                name  = toStr(unified.get("name"));
            }
            if (isBlank(email)) email = ou.getAttribute("email");
            if (isBlank(name))  name  = ou.getAttribute("name");
        }

        if (isBlank(email)) {
            String target = appendParamRespectingHash(redirect, "error", "email_consent_required");
            getRedirectStrategy().sendRedirect(request, response, target);
            return;
        }

        User user = authSer.upsertOAuthUser(email, name);
        String token = jwtTokenProvider.generateToken(user.getUserId(), user.getUserEmail());

        // ★ 해시를 인식해서 token을 fragment query에 붙인다
        String target = appendParamRespectingHash(redirect, "token", token);
        getRedirectStrategy().sendRedirect(request, response, target);
    }

    /* -------- 유틸 -------- */

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
    private static String toStr(Object o) {
        return (o == null) ? null : String.valueOf(o);
    }
    private static String firstNonBlank(String... vals) {
        for (String v : vals) if (!isBlank(v)) return v;
        return null;
    }

    /**
     * redirect가 해시(#)를 포함하면 파라미터를 해시 뒤(fragment)의 쿼리에 붙인다.
     * 예)
     *   http://host/#/login/callback
     *     -> http://host/#/login/callback?key=val
     *   http://host/#/login/callback?x=1
     *     -> http://host/#/login/callback?x=1&key=val
     * 해시가 없으면 일반 쿼리스트링으로 붙인다.
     */
    private static String appendParamRespectingHash(String redirect, String key, String value) {
        String encKey = URLEncoder.encode(key, StandardCharsets.UTF_8);
        String encVal = URLEncoder.encode(value, StandardCharsets.UTF_8);

        int hash = redirect.indexOf('#');
        if (hash >= 0) {
            String base = redirect.substring(0, hash);        // http://host
            String fragment = redirect.substring(hash + 1);   // e.g. /login/callback or /login/callback?x=1
            String sep = (fragment.contains("?") ? "&" : "?");
            return base + "#" + fragment + sep + encKey + "=" + encVal;
        } else {
            String sep = (redirect.contains("?") ? "&" : "?");
            return redirect + sep + encKey + "=" + encVal;
        }
    }
}