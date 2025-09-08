package co.kr.sikim.suinproject.config.oauth;

import java.util.Map;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo{

    private final Map<String, Object> attributes;

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) { this.attributes = attributes; }

    @Override public String getProvider() { return "google"; }
    @Override public String getProviderId() { return (String) attributes.getOrDefault("sub", ""); }
    @Override public String getEmail() { return (String) attributes.getOrDefault("email", ""); }
    @Override public String getName() { return (String) attributes.getOrDefault("name", ""); }
    @Override public Map<String, Object> getAttributes() { return attributes; }
}
