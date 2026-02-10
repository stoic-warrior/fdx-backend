package com.fdx.backend.domain.user;

/**
 * 인증 제공자 타입
 */
public enum AuthProvider {
    LOCAL,   // 일반 이메일/비밀번호 로그인
    GOOGLE,  // Google OAuth
    KAKAO,   // Kakao OAuth
    NAVER    // Naver OAuth
}
