package co.kr.sikim.suinproject.config.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final RedirectWhitelistProperties whitelist;
    private static final String DEFAULT_REDIRECT = "http://localhost:9217/login/callback";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
            throws IOException {
        String redirect = Optional.ofNullable(request.getParameter("redirect_uri"))
                .filter(uri -> whitelist.isAllowed(uri))
                .orElse(DEFAULT_REDIRECT);

        String target = UriComponentsBuilder.fromUriString(redirect)
                .queryParam("error", "oauth2_failure")
                .build(true)
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, target);
    }
}