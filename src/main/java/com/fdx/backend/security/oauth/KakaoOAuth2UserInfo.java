package com.fdx.backend.security.oauth;

import java.util.Map;

/**
 * Kakao OAuth2 사용자 정보
 * Kakao 응답 형식:
 * {
 *   "id": 1234567890,
 *   "kakao_account": {
 *     "email": "user@kakao.com",
 *     "profile": {
 *       "nickname": "홍길동",
 *       "profile_image_url": "https://..."
 *     }
 *   }
 * }
 */
public class KakaoOAuth2UserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = getKakaoAccount();
        if (kakaoAccount == null) return null;
        return (String) kakaoAccount.get("email");
    }

    @Override
    public String getName() {
        Map<String, Object> kakaoAccount = getKakaoAccount();
        if (kakaoAccount == null) return null;

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        if (profile == null) return null;

        return (String) profile.get("nickname");
    }

    @Override
    public String getProfileImageUrl() {
        Map<String, Object> kakaoAccount = getKakaoAccount();
        if (kakaoAccount == null) return null; // null인데 메서드 호출하면 에러이므로

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        if (profile == null) return null;

        return (String) profile.get("profile_image_url");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getKakaoAccount() {
        return (Map<String, Object>) attributes.get("kakao_account");
    }

}
