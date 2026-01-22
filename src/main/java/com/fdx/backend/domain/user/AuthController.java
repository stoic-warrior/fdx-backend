package com.fdx.backend.domain.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 REST API Controller
 *
 * API 엔드포인트:
 * POST /api/auth/signup - 회원가입
 * POST /api/auth/login  - 로그인 (JWT 토큰 발급)
 * GET  /api/auth/me     - 현재 로그인한 사용자 정보 조회
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
}
