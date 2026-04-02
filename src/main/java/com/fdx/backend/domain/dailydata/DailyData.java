package com.fdx.backend.domain.dailydata;


import com.fdx.backend.domain.wig.Wig;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Daily Data (일간 실적) 엔티티
 * Lead Measures의 일간 성과를 기록합니다
 *
 * 리드매셔 값은 DailyLeadData 테이블에서 (lead_measure_id, value)로 관리
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

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, length = 10)
    private String week;

    @Column(length = 10)
    private String dayOfWeek;

    /**
     * 리드매셔별 실적 (정규화)
     * DailyLeadData: (daily_data_id, lead_measure_id, value)
     */
    @OneToMany(mappedBy = "dailyData", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DailyLeadData> leadValues = new ArrayList<>();

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
