package com.fdx.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * DailyData 생성/수정 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyDataRequest {

    @NotNull(message = "날짜는 필수입니다")
    private LocalDate date;

    @NotBlank(message = "주차는 필수입니다")
    private String week;

    private String dayOfWeek;

    /**
     * 리드매셔별 실적 값
     * key: leadMeasureId, value: 실적 값
     * 예: { 1: 7.0, 2: 1.0, 3: 60.0 }
     */
    @Builder.Default
    private Map<Long, Double> leadValues = new HashMap<>();

    @NotNull(message = "WIG ID는 필수입니다")
    private Long wigId;
}
