package com.fdx.backend.security.oauth;

import com.fdx.backend.domain.user.AuthProvider;
import com.fdx.backend.domain.user.Role;
import com.fdx.backend.domain.user.User;
import com.fdx.backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

/**
 * OAuth2 로그인 처리 Service
 * Google, Kakao, Naver 모두 이 하나의 클래스에서 처리!
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional // all or nothing 보장
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. OAuth2 Provider에서 사용자 정보 가져오기
        // super.xx() = 부모클래스의 xx메서드 호출. DefaultOAuth2UserService에 “사용자 정보 가져오는 메서드”가 있음
        /*
         1 access token으로
         2 구글/카카오 API 호출
         3 JSON 사용자 정보 받아옴
         4 Map 형태로 파싱
         5 OAuth2User 객체로 만들어 반환
        */
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. Provider 구분 (google, kakao, naver)
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // 구글/카카오/네이버 중 어디서 왓나
        AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase()); // String을 Enum으로 변환

        // 3. Provider별 사용자 정보 추출
        // 구글, 카카오, 네이버의 사용자 정보 구조가 다른걸 userInfo라는 공통 인터페이스로 바꿈
        OAuth2UserInfo userInfo = getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        log.info("OAuth2 로그인: provider={}, email={}", provider, userInfo.getEmail());

        // 4. DB에서 사용자 조회 또는 신규 생성
        User user = processOAuth2User(provider, userInfo);

        // 5. CustomOAuth2User 반환 (Spring Security에서 사용)
        // 우리DB의 user와 provider에서 온 attributes를 합쳐서 반환
        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }

    /**
     * Provider별 OAuth2UserInfo 생성
     */
    private OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> new GoogleOAuth2UserInfo(attributes);
            case "kakao" -> new KakaoOAuth2UserInfo(attributes);
            case "naver" -> new NaverOAuth2UserInfo(attributes);
            default -> throw new OAuth2AuthenticationException("지원하지 않는 OAuth2 Provider: " + registrationId);
        };
    }

    /**
     * OAuth2 사용자 처리 (조회 또는 생성)
     */
    private User processOAuth2User(AuthProvider provider, OAuth2UserInfo userInfo) {
        String email = userInfo.getEmail();

        // 이메일이 없으면 providerId로 고유 이메일 생성
        if (email == null || email.isBlank()) {
            email = userInfo.getProviderId() + "@" + provider.name().toLowerCase() + ".oauth";
        }

        //로그용 최종 이메일 형태
        String finalEmail = email;

        // 기존 사용자 조회
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            // 같은 Provider인지 확인
            // 다른 provider여도 같은 email인 경우가 있어서 체크함 (ex. 구글에서 네이버연동해서 abc@naver.com)
            if (user.getProvider() != provider) {
                throw new OAuth2AuthenticationException(
                        "이미 " + user.getProvider().name() + "(으)로 가입된 이메일입니다: " + email
                );
            }

            // 사용자 정보 업데이트 (이름, 프로필 이미지)
            return user.updateOAuthInfo(userInfo.getName(), userInfo.getProfileImageUrl());
        }

        // 신규 사용자 생성
        log.info("OAuth2 신규 회원가입: email={}, provider={}", finalEmail, provider);

        User newUser = User.builder()
                .email(finalEmail)
                .name(userInfo.getName() != null ? userInfo.getName() : "사용자")
                .provider(provider)
                .providerId(userInfo.getProviderId())
                .profileImageUrl(userInfo.getProfileImageUrl())
                .role(Role.USER)
                .build();

        return userRepository.save(newUser);
    }
}
