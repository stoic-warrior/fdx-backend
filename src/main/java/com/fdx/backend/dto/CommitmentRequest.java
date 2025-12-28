package com.fdx.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Commitment 생성/수정 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommitmentRequest {

    @NotBlank(message = "약속 내용은 필수입니다")
    private String text;

    @NotBlank(message = "주차는 필수입니다")
    private String week;

    private Boolean completed;

    @NotNull(message = "WIG ID는 필수입니다")
    private Long wigId;
}
