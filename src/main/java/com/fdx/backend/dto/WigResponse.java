package com.fdx.backend.dto;

import com.fdx.backend.domain.MeasureType;
import com.fdx.backend.domain.wig.Wig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * WIG 응답 DTO
 * 엔티티를 그대로 노출하지 않고 필요한 정보만 반환
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WigResponse {

    private Long id;
    private String title;
    private String fromX;
    private String toY;
    private LocalDate byWhen;
    private MeasureType measureType;
    private String unit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity를 DTO로 변환하는 정적 팩토리 메서드
     */
    public static WigResponse from(Wig wig) {
        return WigResponse.builder()
                .id(wig.getId())
                .title(wig.getTitle())
                .fromX(wig.getFromX())
                .toY(wig.getToY())
                .byWhen(wig.getByWhen())
                .measureType(wig.getMeasureType())
                .unit(wig.getUnit())
                .createdAt(wig.getCreatedAt())
                .updatedAt(wig.getUpdatedAt())
                .build();
    }
}