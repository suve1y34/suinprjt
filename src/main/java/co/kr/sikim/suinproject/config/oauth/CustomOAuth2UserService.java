package co.kr.sikim.suinproject.config.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) {
        OAuth2User oAuth2User = super.loadUser(req);
        String registrationId = req.getClientRegistration().getRegistrationId(); // google
        Map<String, Object> attrs = oAuth2User.getAttributes();

        OAuth2UserInfo info = switch (registrationId) {
            case "google" -> new GoogleOAuth2UserInfo(attrs);
            default -> throw new IllegalArgumentException("Unsupported provider: " + registrationId);
        };

        // 스프링 시큐리티가 참조할 사용자명 키 (각 provider의 user-name-attribute와 일치하도록)
        String nameAttributeKey = req.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // 표준화된 속성도 함께 담아 전달(필요 시 success handler에서 사용)
        Map<String, Object> unified = Map.of(
                "provider", info.getProvider(),
                "providerId", info.getProviderId(),
                "email", info.getEmail(),
                "name", info.getName()
        );

        // 기존 attr + unified 병합
        Map<String, Object> merged = new java.util.HashMap<>(attrs);
        merged.put("unified", unified);

        return new DefaultOAuth2User(oAuth2User.getAuthorities(), merged, nameAttributeKey);
    }
}
