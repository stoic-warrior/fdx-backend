package com.fdx.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * 리드매셔별 실적 값
     * key: leadMeasureId, value: 실적 값
     */
    @Builder.Default
    private Map<Long, Double> leadValues = new HashMap<>();

    @NotNull(message = "WIG ID는 필수입니다")
    private Long wigId;
}
