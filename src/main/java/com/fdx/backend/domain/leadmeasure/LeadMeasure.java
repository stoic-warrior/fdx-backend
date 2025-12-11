package com.fdx.backend.domain.leadmeasure;

import com.fdx.backend.domain.wig.Wig;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Lead Measure (선행지표) 엔티티
 * 4DX: 목표(Lag Measure) 달성을 위한 핵심 활동 지표
 *
 * 예시:
 * - 목표가 "체중 감량"이면, Lead Measure는 "운동 시간", "칼로리 섭취"
 * - 목표가 "취업"이면, Lead Measure는 "코딩 시간", "이력서 제출"
 */
@Entity// jpa 엔티티임을 선언. jpa가 조회함
@Table(name = "lead_measures")// 매핑될 테이블명
@Getter // jpa는 getter를 이용한 프록시 접근도 많음
@Setter // 프로토타입때 사용하다 나중에 리팩토링
@NoArgsConstructor  // JPA가 엔티티를 프록시로 생성할 때, 파라미터 없는 기본 생성자가 필요
@AllArgsConstructor // 필수는 아님. 테스트시 사용
@Builder// 안정적이고 가독성 높은 객체 생성 방식 제공
public class LeadMeasure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 선행지표 이름
     * 예: "코딩 시간", "운동 시간", "이력서 제출"
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 일일 목표값
     * 예: 하루 6시간 코딩, 하루 60분 운동
     */
    @Column(nullable = false)
    private Double dailyTarget;

    /**
     * 주간 목표값
     * 예: 주 42시간 코딩 (6시간 x 7일), 주 420분 운동 (60분 x 7일)
     */
    @Column(nullable = false)
    private Double weeklyTarget;

    /**
     * 단위
     * 예: "시간", "분", "개", "kcal"
     */
    @Column(nullable = false, length = 20)
    private String unit;

    /**
     * 소속 WIG (Many-to-One)
     * 한 WIG는 여러 Lead Measures를 가질 수 있음
     */
    @ManyToOne(fetch = FetchType.LAZY)  // 여러 리드매셔가 한 wig에 속한다는 뜻
    // 디폴트인 EAGER면 Wig 테이블까지 같이 조인 / 추가 쿼리 하므로 LAZY설정
    @JoinColumn(name = "wig_id", nullable = false) // “lead_measures 테이블에 wig_id라는 컬럼을 만들고, 이 컬럼이 외래키(FK)다.”
    private Wig wig;

    @CreationTimestamp // INSERT 시 자동으로 timestamp 입력.
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // update 시 Hibernate가 자동 업데이트
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 편의 메서드: 이 Lead Measure의 WIG 설정
     * 양방향 관계 동기화
     */
    public void setWig(Wig wig) {
        this.wig = wig; // 이거(리드매셜)에 위그 연결
        if (wig != null && !wig.getLeadMeasures().contains(this)) {
            wig.getLeadMeasures().add(this); // 위그 리드매셜 목록에 이거 연결
        }
    }
}
