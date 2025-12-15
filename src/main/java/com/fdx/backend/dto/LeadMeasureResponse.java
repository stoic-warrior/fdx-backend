package com.fdx.backend.dto;

import com.fdx.backend.domain.leadmeasure.LeadMeasure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Lead Measure 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadMeasureResponse {

    private Long id;
    private String name;
    private Double dailyTarget;
    private Double weeklyTarget;
    private String unit;
    private Long wigId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity를 DTO로 변환하는 정적 팩토리 메서드
     */
    public static LeadMeasureResponse from(LeadMeasure leadMeasure) {
        return LeadMeasureResponse.builder()
                .id(leadMeasure.getId())
                .name(leadMeasure.getName())
                .dailyTarget(leadMeasure.getDailyTarget())
                .weeklyTarget(leadMeasure.getWeeklyTarget())
                .unit(leadMeasure.getUnit())
                .wigId(leadMeasure.getWig().getId())
                .createdAt(leadMeasure.getCreatedAt())
                .updatedAt(leadMeasure.getUpdatedAt())
                .build();
    }


}
