package com.fdx.backend.domain.weeklydata;

import com.fdx.backend.domain.wig.Wig;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Weekly Data (주간 실적) 엔티티
 * WIG와 Lead Measures의 주간 성과를 기록합니다
 *
 * STATE 타입: milestoneProgress 사용
 * NUMERIC 타입: actual, target 사용
 * 리드매셔 값은 WeeklyLeadData 테이블에서 (lead_measure_id, value)로 관리
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

    @Column(nullable = false, length = 10)
    private String week;

    @Column
    private Double milestoneProgress;

    @Column
    private Double actual;

    @Column
    private Double target;

    /**
     * 리드매셔별 실적 (정규화)
     * WeeklyLeadData: (weekly_data_id, lead_measure_id, value)
     */
    @OneToMany(mappedBy = "weeklyData", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WeeklyLeadData> leadValues = new ArrayList<>();

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
