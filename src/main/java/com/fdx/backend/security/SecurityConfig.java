package com.fdx.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 설정
 */
@Configuration // 스프링 설정 클래스
@EnableWebSecurity // Spring Security 활성화 + 이 클래스를 보안 설정으로
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // 요청 들어올 때 Authorization: Bearer 토큰 꺼내서 검증하고, 맞으면 SecurityContext에 인증 객체 넣어주는 필터(보통)
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint; // 인증이 필요한데 인증이 없거나 실패했을 때(401) 어떤 응답을 줄지 담당

    @Bean
    public SecurityFilterChain filterchain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (JWT 사용하므로). CSRF는 세션/쿠키 기반 로그인에서 주로 문제됨
                .csrf(csrf -> csrf.disable())

                // CORS 설정. 아래의 corsConfigurationSource() Bean을 쓰라는 뜻
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 세션 사용 안함 (JWT 사용). STATELESS = 서버가 세션을 저장/조회하지 않음. 즉, 요청마다 토큰으로 인증한다.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // URL별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",           // 로그인, 회원가입
                                "/h2-console/**",         // H2 콘솔
                                "/swagger-ui/**",         // Swagger UI
                                "/swagger-ui.html",
                                "/api-docs/**",           // API 문서
                                "/v3/api-docs/**"
                        ).permitAll() // 로그인 없이도 접근 허용
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated() // 위에 나열한 것 말고는 전부 인증 필요
                ) // 여기서 “인증”은 보통 SecurityContext에 Authentication이 채워진 상태를 의미하고, 그걸 만드는 게 바로 JWT 필터 역할.

        // 인증 실패 처리
                .exceptionHandling()
    }


}
