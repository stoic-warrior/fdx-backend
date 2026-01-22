package com.fdx.backend.domain.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@RestController // @Controller + @ResponseBody, 리턴값이 전부 JSON으로 직렬화
@RequestMapping("/api/auth") // 이 컨트롤러의 공통 URL prefix
@RequiredArgsConstructor // final필드 생성자로 주입
@Slf4j // log.info() 쓰기 위한 로거
@Tag(name = "Auth", description = "인증 API (회원가입, 로그인)") // Swagger(OpenAPI) 문서용
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입
     * POST /api/auth/signup
     */
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (이메일 중복 등)")
    })
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest request) {
        log.info("POST /api/auth/signup - 회원가입 요청: {}", request.getEmail());
        UserResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 로그인
     * POST /api/auth/login
     */
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login - 로그인 요청: {}", request.getEmail());
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
