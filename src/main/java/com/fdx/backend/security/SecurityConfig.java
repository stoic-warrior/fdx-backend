package com.fdx.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Spring Security 설정
 */
@Configuration // 스프링 설정 클래스
@EnableWebSecurity // Spring Security 활성화 + 이 클래스를 보안 설정으로
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter
}
