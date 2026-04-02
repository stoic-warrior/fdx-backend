package com.fdx.backend.domain.dailydata;

import com.fdx.backend.domain.leadmeasure.LeadMeasure;
import jakarta.persistence.*;
import lombok.*;

/**
 * DailyData와 LeadMeasure를 연결하는 정규화 테이블
 * 기존 lead1~lead5 컬럼 대신, (daily_data_id, lead_measure_id, value)로 관리
 */
@Entity
@Table(name = "daily_lead_data",
        uniqueConstraints = @UniqueConstraint(columnNames = {"daily_data_id", "lead_measure_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyLeadData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_data_id", nullable = false)
    private DailyData dailyData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_measure_id", nullable = false)
    private LeadMeasure leadMeasure;

    /**
     * 해당 리드매셔의 실적 값
     * 예: 코딩 7시간, 이력서 1개
     */
    @Column(name = "lead_value")
    private Double value;
}
