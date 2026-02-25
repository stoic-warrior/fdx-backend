package com.fdx.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 인증 필터
 * 모든 HTTP 요청에서 JWT 토큰을 확인하고 인증 처리
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter { // http요청 1회당 1번만 실행

    private final JwtTokenProvider jwtTokenProvider; // jwt 검증은 얘가 함

    //모든 HTTP 요청에, SecurityFilterChain 안에서 호출되는 메서드
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // OAuth 관련 경로는 필터 스킵
        String path = request.getRequestURI();
        if (path.startsWith("/oauth2/") || path.startsWith("/login/oauth2/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. Request Header에서 JWT 토큰 추출
        String bearerToken = request.getHeader("Authorization"); // 헤서에서 key가 Authorization인 값 추출. "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6..."
        String token = jwtTokenProvider.resolveToken(bearerToken); // "Bearer " 제거, 순수 JWT 문자열만 반환

        // 2. 토큰 유효성 검증
        if (token != null && jwtTokenProvider.validateToken(token)) { // 서명검증, 만료시간확인, 토큰구조 파싱가능여부 등등

            // 3. 토큰이 유효하면 Authentication 객체를 가져와서 SecurityContext에 저장
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Security Context에 '{}' 인증 정보 저장", authentication.getName()); // Spring Security에 이 사용자 인증된거 알려줌
        }

        // 4. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);

    }

    }
