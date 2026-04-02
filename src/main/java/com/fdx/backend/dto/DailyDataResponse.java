package com.fdx.backend.dto;

import com.fdx.backend.domain.dailydata.DailyData;
import com.fdx.backend.domain.dailydata.DailyLeadData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DailyData 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyDataResponse {

    private Long id;
    private LocalDate date;
    private String week;
    private String dayOfWeek;

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
    public static DailyDataResponse from(DailyData dailyData) {
        Map<Long, Double> leadValues = dailyData.getLeadValues().stream()
                .collect(Collectors.toMap(
                        dld -> dld.getLeadMeasure().getId(),
                        DailyLeadData::getValue,
                        (v1, v2) -> v2
                ));

        return DailyDataResponse.builder()
                .id(dailyData.getId())
                .date(dailyData.getDate())
                .week(dailyData.getWeek())
                .dayOfWeek(dailyData.getDayOfWeek())
                .leadValues(leadValues)
                .wigId(dailyData.getWig().getId())
                .createdAt(dailyData.getCreatedAt())
                .updatedAt(dailyData.getUpdatedAt())
                .build();
    }
}
