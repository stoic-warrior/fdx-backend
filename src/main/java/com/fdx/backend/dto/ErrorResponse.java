package com.fdx.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
     * 예: "WIG_NOT_FOUND", "MAX_WIG_EXCEEDED"
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

    public static ErrorResponse of(String code,String message, int status) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .status(status)
                .build();
    }
}
