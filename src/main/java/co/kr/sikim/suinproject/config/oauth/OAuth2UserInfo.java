package co.kr.sikim.suinproject.config.oauth;

import java.util.Map;

public interface OAuth2UserInfo {
    String getProvider(); // google | kakao | naver
    String getProviderId();
    String getEmail();
    String getName();
    Map<String, Object> getAttributes();
}
