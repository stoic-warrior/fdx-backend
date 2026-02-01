package com.fdx.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JWT 토큰 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponse {

    private String accessToken;
    private String tokenType;
    private Long userId;
    private String email;
    private String name;

    public static TokenResponse of(String accessToken, Long userId, String email, String name) {
        return TokenResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .userId(userId)
                .email(email)
                .name(name)
                .build();
    }
}
