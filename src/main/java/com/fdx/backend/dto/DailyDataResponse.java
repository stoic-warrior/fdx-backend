package com.fdx.backend.dto;

import com.fdx.backend.domain.dailydata.DailyData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private Double lead1;
    private Double lead2;
    private Long wigId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity를 DTO로 변환하는 정적 팩토리 메서드
     */
    public static DailyDataResponse from(DailyData dailyData) {
        return DailyDataResponse.builder()
                .id(dailyData.getId())
                .date(dailyData.getDate())
                .week(dailyData.getWeek())
                .dayOfWeek(dailyData.getDayOfWeek())
                .lead1(dailyData.getLead1())
                .lead2(dailyData.getLead2())
                .wigId(dailyData.getWig().getId())
                .createdAt(dailyData.getCreatedAt())
                .updatedAt(dailyData.getUpdatedAt())
                .build();
    }
}
