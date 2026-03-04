package com.fdx.backend.security.oauth;

/**
 * OAuth2 Provider별 사용자 정보 추상화
 * Google, Kakao, Naver 각각 응답 형식이 다르므로 통일된 인터페이스 제공
 */
public interface OAuth2UserInfo {
    String getProviderId();
    String getEmail();
    String getName();
    String getProfileImageUrl();
}
