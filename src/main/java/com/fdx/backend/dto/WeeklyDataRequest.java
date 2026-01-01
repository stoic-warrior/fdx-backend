package com.fdx.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WeeklyData 생성/수정 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyDataRequest {

    @NotBlank(message = "주차는 필수입니다")
    private String week;

    // STATE 타입 WIG용
    private Double milestoneProgress;

    // NUMERIC 타입 WIG용
    private Double actual;
    private Double target;

    // Lead Measures 실적
    private Double lead1;
    private Double lead2;

    @NotNull(message = "WIG ID는 필수입니다")
    private Long wigId;
}
