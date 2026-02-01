package com.fdx.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fdx.backend.dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증되지 않은 요청 처리
 * JWT 토큰이 없거나 유효하지 않을 때 호출됨
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // 빈이 아니므로 @RequiredArgsConstructor안씀
    private final ObjectMapper objectMapper = new ObjectMapper() // Java 객체 → JSON 문자열 변환기
            .registerModule(new JavaTimeModule());


    // 인증실패시 이게 호출
    @Override
    public void commence(HttpServletRequest request, // 실패한 요청, 여기선 안쓰이지만 Spring Security가 강제로 요구하는 인터페이스 계약
                         HttpServletResponse response, // 우리가 직접 응답 써야 할 대상
                         AuthenticationException authException) // 왜 인증 실패했는지 이유
            throws IOException, ServletException {

        log.error("인증 실패: {}", authException.getMessage());

        ErrorResponse errorResponse = ErrorResponse.of(
                "UNAUTHORIZED",
                "인증이 필요합니다. 로그인 후 다시 시도해주세요.",
                HttpStatus.UNAUTHORIZED.value()
        );

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse)); // 응답 바디 작성
    }

}
