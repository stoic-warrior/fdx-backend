package com.fdx.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 표준화된 에러 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    /**
     * 에러 코드
     * 예: "WIG_NOT_FOUND", "MAX_WIG_EXCEEDED", "VALIDATION_FAILED"
     */
    private String code;

    /**
     * 사용자에게 보여줄 메시지
     */
    private String message;

    /**
     * 에러 발생 시각
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * 추가 정보 (선택)
     */
    private String details;

    /**
     * HTTP 상태 코드
     */
    private int status;

    /**
     * Validation 에러 필드별 상세 정보 (선택)
     * 예: {"title": "목표 제목은 필수입니다", "fromX": "시작 상태는 필수입니다"}
     */
    private Map<String, String> fieldErrors;

    public static ErrorResponse of(String code, String message, int status) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .status(status)
                .build();
    }

    public static ErrorResponse of(String code, String message, int status, Map<String,String> fieldErrors) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .status(status)
                .fieldErrors(fieldErrors)
                .build();
    }
}
