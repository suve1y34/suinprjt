package co.kr.sikim.suinproject.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        log.debug("[JWT] >>> {} {}", request.getMethod(), request.getRequestURI());

        // 1) Authorization 헤더 확인 (Bearer 접두사 필수)
        String authz = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authz) || !authz.startsWith("Bearer ")) {
            log.debug("[JWT] no/invalid Authorization header -> pass through");
            filterChain.doFilter(request, response);
            return;
        }

        // 2) 토큰 추출
        String token = authz.substring(7);

        try {
            // 3) 유효성 검사 (서명/만료 등)
            if (!tokenProvider.validateToken(token)) {
                log.debug("[JWT] validateToken=false");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }

            // 4) 사용자 식별값 추출 (현재 sub = userId 가정)
            Long userId = tokenProvider.getUserId(token);     // sub -> Long

            // 5) 이미 세팅되어 있지 않으면 Authentication 설정
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                // principal: 간단히 userId만 쓰셔도 되고,
                                // 커스텀 LoginUser(userId, email) 객체를 써도 됩니다.
                                userId,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("[JWT] authenticated uid={}", userId);
            }

            // 6) 다음 필터 진행
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // 파싱/검증 중 예외는 익명으로 흘리지 말고 401로 고정
            log.debug("[JWT] parse/verify failed: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
        } finally {
            log.debug("[JWT] <<< {} {}", request.getMethod(), request.getRequestURI());
        }
    }
}
