package com.fdx.backend.dto;

import com.fdx.backend.domain.MeasureType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * WIG 생성/수정 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WigRequest {

    @NotBlank(message = "목표 제목은 필수입니다")
    private String title;

    @NotBlank(message = "시작 상태는 필수입니다")
    private String fromX;

    @NotBlank(message = "목표 상태는 필수입니다")
    private String toY;

    @NotNull(message = "목표 기한은 필수입니다")
    private LocalDate byWhen;

    @NotNull(message = "측정 유형은 필수입니다")
    private MeasureType measureType;

    private String unit;  // measureType이 NUMERIC일 때만 필요
}