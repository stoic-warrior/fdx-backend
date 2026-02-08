package com.fdx.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security 설정
 */
@Configuration // 스프링 설정 클래스
@EnableWebSecurity // Spring Security 활성화 + 이 클래스를 보안 설정으로
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // HTTP요청 들어올 때 Jwt토큰 검증하고, 맞으면 SecurityContext에 인증 객체 넣어주는 필터
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint; // 인증이 없거나 실패했을 때(401) 어떤 응답을 줄지 담당

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
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
                        .anyRequest().authenticated() // 위에 나열한 것 말고는 전부 인증 필요
                ) // 여기서 “인증”은 보통 SecurityContext에 Authentication이 채워진 상태를 의미하고, 그걸 만드는 게 바로 JWT 필터 역할.

                // 인증 실패 처리 (401 커스텀)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))

                // H2 콘솔용 설정 (iframe 허용). H2 콘솔은 브라우저에서 iframe을 쓰는 경우가 있어 X-Frame-Options 때문에 막힐 수 있음. sameOrigin()으로 같은 오리진에서의 iframe 렌더는 허용.
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin()))

                // JWT 필터 추가. '“jwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter보다 앞에 배치하라”
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",  // React 개발 서버
                "http://localhost:5173"   // Vite 개발 서버
                "https://fdx-frontend.vercel.app/"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization")); // 클라이언트에서 Authorization 헤더 접근 허용
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 비밀번호 암호화 (BCrypt)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager 빈 등록
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


}
