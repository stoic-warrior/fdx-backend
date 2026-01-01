package com.fdx.backend.dto;

import com.fdx.backend.domain.weeklydata.WeeklyData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * WeeklyData 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyDataResponse {


    private Long id;
    private String week;
    private Double milestoneProgress;
    private Double actual;
    private Double target;
    private Double lead1;
    private Double lead2;
    private Long wigId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity를 DTO로 변환하는 정적 팩토리 메서드
     */
    public static WeeklyDataResponse from(WeeklyData weeklyData) {
        return WeeklyDataResponse.builder()
                .id(weeklyData.getId())
                .week(weeklyData.getWeek())
                .milestoneProgress(weeklyData.getMilestoneProgress())
                .actual(weeklyData.getActual())
                .target(weeklyData.getTarget())
                .lead1(weeklyData.getLead1())
                .lead2(weeklyData.getLead2())
                .wigId(weeklyData.getWig().getId())
                .createdAt(weeklyData.getCreatedAt())
                .updatedAt(weeklyData.getUpdatedAt())
                .build();
    }
}
