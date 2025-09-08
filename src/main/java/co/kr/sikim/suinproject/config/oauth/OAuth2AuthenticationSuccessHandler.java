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
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> unified = (Map<String, Object>) principal.getAttributes().get("unified");
        String email = unified != null ? String.valueOf(unified.getOrDefault("email", "")) : "";
        String name = unified != null ? String.valueOf(unified.getOrDefault("name", "")) : "";

        if (email == null || email.isBlank()) {
            // 이메일 동의 안 된 케이스: provider id를 임시 이메일로 대체하는 등 별도 정책 필요
            // 여기서는 단순 실패 리다이렉트
            failureRedirect(response, request, "email_consent_required");
            return;
        }

        // 1) 사용자 조회/생성
        User user = authSer.upsertOAuthUser(email, name);

        // 2) JWT 발급
        String token = jwtTokenProvider.generateToken(user.getUserId(), user.getUserEmail());

        // 3) redirect_uri 검사(화이트리스트)
        String redirect = Optional.ofNullable(request.getParameter("redirect_uri"))
                .filter(uri -> whitelist.isAllowed(uri))
                .orElse(DEFAULT_REDIRECT);

        String target = UriComponentsBuilder.fromUriString(redirect)
                .queryParam("token", token)
                .build(true)
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, target);
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