package com.fdx.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 연속달성(Streak) 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StreakResponse {

    /** 모든 lead measure를 동시에 달성한 연속 일수 */
    private int overallStreak;

    /** lead measure별 개별 streak */
    private List<LeadMeasureStreak> leadMeasureStreaks;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LeadMeasureStreak {
        private Long leadMeasureId;
        private String name;
        private int currentStreak;
        private String direction; // "MAXIMIZE" or "MINIMIZE"
    }
}
