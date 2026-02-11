package com.fdx.backend.security.oauth;

import java.util.Map;

/**
 * Naver OAuth2 사용자 정보
 * Naver 응답 형식:
 * {
 *   "resultcode": "00",
 *   "message": "success",
 *   "response": {
 *     "id": "abc123",
 *     "email": "user@naver.com",
 *     "name": "홍길동",
 *     "profile_image": "https://..."
 *   }
 * }
 */
public class NaverOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        // Naver는 "response" 안에 실제 정보가 있음
        this.attributes = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
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
        return (String) attributes.get("profile_image");
    }
}
