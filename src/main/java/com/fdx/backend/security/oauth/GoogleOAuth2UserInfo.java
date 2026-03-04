package com.fdx.backend.security.oauth;

import java.util.Map;
/**
 * Google OAuth2 사용자 정보
 * Google 응답 형식:
 * {
 *   "sub": "1234567890",
 *   "email": "user@gmail.com",
 *   "name": "홍길동",
 *   "picture": "https://..."
 * }
 */
public class GoogleOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getProfileImageUrl() {
        return (String) attributes.get("picture");
    }

}
