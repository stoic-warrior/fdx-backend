package com.fdx.backend.domain.weeklydata;

import com.fdx.backend.domain.leadmeasure.LeadMeasure;
import jakarta.persistence.*;
import lombok.*;

/**
 * WeeklyData와 LeadMeasure를 연결하는 정규화 테이블
 * 기존 lead1~lead5 컬럼 대신, (weekly_data_id, lead_measure_id, value)로 관리
 */
@Entity
@Table(name = "weekly_lead_data",
        uniqueConstraints = @UniqueConstraint(columnNames = {"weekly_data_id", "lead_measure_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyLeadData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weekly_data_id", nullable = false)
    private WeeklyData weeklyData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_measure_id", nullable = false)
    private LeadMeasure leadMeasure;

    /**
     * 해당 리드매셔의 주간 실적 값
     */
    @Column(name = "lead_value")
    private Double value;
}
