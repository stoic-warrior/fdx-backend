package com.fdx.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    // Lead Measures 실적
    private Double lead1;
    private Double lead2;

    @NotNull(message = "WIG ID는 필수입니다")
    private Long wigId;
}
