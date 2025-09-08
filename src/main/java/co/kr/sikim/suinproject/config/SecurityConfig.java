package co.kr.sikim.suinproject.config;

import co.kr.sikim.suinproject.config.oauth.CustomOAuth2UserService;
import co.kr.sikim.suinproject.config.oauth.OAuth2AuthenticationFailureHandler;
import co.kr.sikim.suinproject.config.oauth.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // ✅ REST API면 끕니다
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/search/**", "/oauth2/authorization/**", "/login/oauth2/code/**", "/error").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // ✅ 프리플라이트 허용
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(u -> u.userService(customOAuth2UserService)) // 프로필 파싱 서비스
                        .successHandler(oAuth2AuthenticationSuccessHandler)            // 성공 시 JWT 발급 + redirect
                        .failureHandler(oAuth2AuthenticationFailureHandler)            // 실패 시 redirect
                );;
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        var cfg = new org.springframework.web.cors.CorsConfiguration();
        cfg.setAllowedOriginPatterns(java.util.List.of("*")); // 필요 시 도메인 지정
        cfg.setAllowedMethods(java.util.List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(java.util.List.of("Authorization","Content-Type","X-Requested-With"));
        cfg.setAllowCredentials(true);
        var src = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }
}
