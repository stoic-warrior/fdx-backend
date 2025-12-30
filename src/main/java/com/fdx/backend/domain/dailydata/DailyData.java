package com.fdx.backend.domain.dailydata;


import com.fdx.backend.domain.wig.Wig;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Daily Data (일간 실적) 엔티티
 * Lead Measures의 일간 성과를 기록합니다
 */
@Entity
@Table(name = "daily_data",
        uniqueConstraints = @UniqueConstraint(columnNames = {"wig_id", "date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 날짜
     */
    @Column(nullable = false)
    private LocalDate date;

    /**
     * 주차 (예: "W1", "W2")
     * 조회 편의성을 위해 추가
     */
    @Column(nullable = false, length = 10)
    private String week;

    /**
     * 요일 (예: "월", "화", "수")
     * UI 표시용
     */
    @Column(length = 10)
    private String dayOfWeek;

    /**
     * Lead Measure 1 실적
     * 예: 하루 코딩 시간 7시간
     */
    @Column
    private Double lead1;

    /**
     * Lead Measure 2 실적
     * 예: 이력서 제출 1개
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
