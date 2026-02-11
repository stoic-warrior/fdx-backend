package com.fdx.backend.security.oauth;

import com.fdx.backend.domain.user.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * OAuth2 인증된 사용자 정보
 * Spring Security에서 사용하는 OAuth2User 구현
 *
 *  * 1) OAuth 제공자가 준 원본 정보(attributes)
 *  * 2) 우리 DB에 저장된 User 엔티티(user)
 *  * 이 두 개를 한 덩어리로 묶어서
 *  * Spring Security가 이해할 수 있는 OAuth2User로 만들어 주는 클래스
 */
@Getter //
public class CustomOAuth2User implements OAuth2User {

    private final User user; // 우리 DB가 쓰는 사용자 엔티티
    private final Map<String, Object> attributes; // OAuth가 준 사용자 정보

    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    // 🔹 OAuth 원본 데이터 반환
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // Spring Security 규칙:
        // 반드시 "ROLE_" 접두어가 붙어야 함
        // 예: ROLE_USER, ROLE_ADMIN
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        // 👉 singletonList는 "요소 1개짜리 리스트" 생성
        // 👉 즉, 권한 1개만 가진 사용자라고 보면 됨
    }

    @Override
    public String getName() {

        // 👉 Spring Security 내부에서
        // "이 사용자를 구분하는 고유 값"으로 사용
        return user.getEmail();
    }

    public Long getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }
}
