package co.kr.sikim.suinproject.config.oauth;

import co.kr.sikim.suinproject.config.JwtTokenProvider;
import co.kr.sikim.suinproject.domain.Bookshelf;
import co.kr.sikim.suinproject.domain.User;
import co.kr.sikim.suinproject.mapper.ShelfMapper;
import co.kr.sikim.suinproject.mapper.UserMapper;
import co.kr.sikim.suinproject.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final AuthService authSer;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedirectWhitelistProperties whitelist;

    private static final String DEFAULT_REDIRECT = "http://localhost:9217/login/callback";

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String email = null;
        String name  = null;

        Object principal = authentication.getPrincipal();

        if (principal instanceof OidcUser oidc) {
            // OIDC 경로: 표준 클레임 활용
            email = oidc.getEmail();

            // getAttribute는 제네릭이라 String으로 직접 받기
            String fullName  = oidc.getFullName();
            String nameAttr  = oidc.getAttribute("name");      // String
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
            failureRedirect(response, request, "email_consent_required");
            return;
        }

        User user = authSer.upsertOAuthUser(email, name);
        String token = jwtTokenProvider.generateToken(user.getUserId(), user.getUserEmail());

        String redirect = Optional.ofNullable(request.getParameter("redirect_uri"))
                .filter(uri -> whitelist.isAllowed(uri))
                .orElse(DEFAULT_REDIRECT);

        String target = UriComponentsBuilder.fromUriString(redirect)
                .queryParam("token", token)
                .build(true)
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, target);
    }

    /* ---- 유틸 ---- */
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

    private void failureRedirect(HttpServletResponse response, HttpServletRequest request, String code) throws IOException {
        String redirect = Optional.ofNullable(request.getParameter("redirect_uri"))
                .filter(uri -> whitelist.isAllowed(uri))
                .orElse(DEFAULT_REDIRECT);

        String target = UriComponentsBuilder.fromUriString(redirect)
                .queryParam("error", code)
                .build(true)
                .toUriString();
        getRedirectStrategy().sendRedirect(request, response, target);
    }
}