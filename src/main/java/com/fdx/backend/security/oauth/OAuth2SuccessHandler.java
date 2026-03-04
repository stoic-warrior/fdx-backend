package com.fdx.backend.security.oauth;

import com.fdx.backend.domain.user.User;
import com.fdx.backend.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * OAuth2 로그인 성공 시 처리
 * JWT 토큰 발급 후 프론트엔드로 리다이렉트
 * 프로필 이미지 URL도 함께 전달
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler { // Spring Security에서 제공하는 로그인 성공 후 처리 기본 클래스

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.frontend-url}") // application.yml 에서 프론트 주소 가져옴
    private String frontendUrl;

    // 사용자가 OAuth 로그인 성공하면 이 메서드가 실행된다.
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // 인증 객체에서 사용자 꺼내기
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oAuth2User.getUser();

        log.info("OAuth2 로그인 성공: email={}, provider={}", user.getEmail(), user.getProvider());

        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole().name());

        // 프론트엔드로 토큰과 함께 리다이렉트
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(frontendUrl + "/oauth/callback")
                .queryParam("token", token)
                .queryParam("email", user.getEmail())
                .queryParam("name", URLEncoder.encode(user.getName(), StandardCharsets.UTF_8))
                .queryParam("provider", user.getProvider().name());

        // 프로필 이미지가 있으면 추가
        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isBlank()) {
            builder.queryParam("profileImageUrl",
                    URLEncoder.encode(user.getProfileImageUrl(), StandardCharsets.UTF_8));
        }

        // 최종 URL 생성
        String targetUrl = builder.build().toUriString();

        log.info("OAuth2 리다이렉트: {}", targetUrl);
        // 리다이렉트 실행. 브라우저를 프론트엔드로 강제 이동시킴.
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
