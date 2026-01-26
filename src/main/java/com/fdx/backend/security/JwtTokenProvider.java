package com.fdx.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증 유틸리티
 */
@Component // 스프링 빈으로 등록
@Slf4j // 로거
@RequiredArgsConstructor // final필드 주입
public class JwtTokenProvider {

    @Value("${jwt.secret}") // application.yml에서 주입
    private String secretKey;

    @Value("${jwt.expiration}")
    private long validityInMilliseconds; // 토큰 유효 시간

    private SecretKey key; // JWT 서명(Signature)에 실제로 쓰이는 키, 문자열(secretKey)을 그대로 쓰지 않고, 👉 Base64 → byte[] → HMAC Key 로 변환해서 사용

    // JWT 안에는 이메일(식별자) 만 있음
    // 하지만 Spring Security는 UserDetails 객체를 필요로 함
    // 그래서 토큰 → 이메일 → DB 조회 → UserDetails 생성 흐름이 필요
    private final UserDetailsService userDetailsService;


    @PostConstruct // 빈이 생성되고 의존성 주입이 끝난 후 딱 1번 실행되는 메서드
    protected void init() {
        // Base64로 인코딩된 시크릿 키를 디코딩하여 SecretKey 생성
        byte[] keyBytes = Base64.getDecoder().decode(secretKey); // Base64 문자열 → **원래 바이트 배열(byte[])**로 복원
        this.key = Keys.hmacShaKeyFor(keyBytes); // keyBytes를 가지고 HMAC-SHA용 SecretKey 객체를 만들어줌
    }

    /**
     * JWT 토큰 생성
     */
    public String createToken(String email, String role) {
        Claims claims = Jwts.claims().subject(email);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .claims(claims)
                .claim("role", role)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();

    }

    /**
     * 토큰에서 이메일 추출
     */
    public String getEmail(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * 토큰으로 Authentication 객체 생성
     */
    public Authentication getAuthentication(String token) { // JWT → Spring Security 인증 객체 변환
        String email = getEmail(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("유효하지 않은 JWT 토큰: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Request Header에서 토큰 추출
     * "Bearer {token}" 형식에서 토큰만 추출
     */
    public String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
