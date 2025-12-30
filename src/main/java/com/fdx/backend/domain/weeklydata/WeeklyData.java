package com.fdx.backend.domain.weeklydata;

import com.fdx.backend.domain.wig.Wig;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Weekly Data (주간 실적) 엔티티
 * WIG와 Lead Measures의 주간 성과를 기록합니다
 *
 * STATE 타입: milestoneProgress 사용
 * NUMERIC 타입: actual, target 사용
 */
@Entity
@Table(name = "weekly_data", uniqueConstraints = @UniqueConstraint(columnNames = {"wig_id", "week"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 주차 (예: "W1", "W2", "W5")
     */
    @Column(nullable = false, length = 10)
    private String week;

    /**
     * Milestone 진행률 (STATE 타입 WIG 전용)
     * 0~100 사이의 값
     */
    @Column
    private Double milestoneProgress;

    /**
     * 실제 달성값 (NUMERIC 타입 WIG 전용)
     * 예: 체중 74.5kg
     */
    @Column
    private Double actual;

    /**
     * 목표값 (NUMERIC 타입 WIG 전용)
     * 예: 목표 체중 73kg
     */
    @Column
    private Double target;

    /**
     * Lead Measure 1 실적
     * 예: 코딩 시간 35시간
     */
    @Column
    private Double lead1;

    /**
     * Lead Measure 2 실적
     * 예: 이력서 제출 2개
     */
    @Column
    private Double lead2;

    /**
     * 소속 WIG (Many-to-One)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wig_id", nullable = false)
    private Wig wig;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;


}
