package com.fdx.backend.dto;

import com.fdx.backend.domain.weeklydata.WeeklyData;
import com.fdx.backend.domain.weeklydata.WeeklyLeadData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 리드매셔별 실적 값
     * key: leadMeasureId, value: 실적 값
     */
    @Builder.Default
    private Map<Long, Double> leadValues = new HashMap<>();

    private Long wigId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity를 DTO로 변환하는 정적 팩토리 메서드
     */
    public static WeeklyDataResponse from(WeeklyData weeklyData) {
        Map<Long, Double> leadValues = weeklyData.getLeadValues().stream()
                .collect(Collectors.toMap(
                        wld -> wld.getLeadMeasure().getId(),
                        WeeklyLeadData::getValue,
                        (v1, v2) -> v2
                ));

        return WeeklyDataResponse.builder()
                .id(weeklyData.getId())
                .week(weeklyData.getWeek())
                .milestoneProgress(weeklyData.getMilestoneProgress())
                .actual(weeklyData.getActual())
                .target(weeklyData.getTarget())
                .leadValues(leadValues)
                .wigId(weeklyData.getWig().getId())
                .createdAt(weeklyData.getCreatedAt())
                .updatedAt(weeklyData.getUpdatedAt())
                .build();
    }
}
