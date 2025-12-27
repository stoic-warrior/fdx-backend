package com.fdx.backend.domain.wig;

import com.fdx.backend.domain.MeasureType;
import com.fdx.backend.domain.commitment.Commitment;
import com.fdx.backend.domain.leadmeasure.LeadMeasure;
import com.fdx.backend.domain.milestone.Milestone;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * WIG (Wildly Important Goal) 엔티티
 * 4DX의 핵심 목표를 나타냅니다
 */
@Entity // jpa 엔티티임을 선언. jpa가 조회함
@Table(name = "wigs") // 매핑될 테이블명
@Getter // jpa는 getter를 이용한 프록시 접근도 많음
@Setter // 프로토타입때 사용하다 나중에 리팩토링
@NoArgsConstructor // JPA가 엔티티를 프록시로 생성할 때, 파라미터 없는 기본 생성자가 필요
@AllArgsConstructor // 필수는 아님. 테스트시 사용
@Builder // 안정적이고 가독성 높은 객체 생성 방식 제공
public class Wig {

    @Id // 이 필드가 PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment로 생성되는 PK. insert할때 id코드 안넣어도 알아서 1,2,3... 증가값 생성
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;   // 목표 제목 (예: "백엔드 개발자 취업")

    @Column(nullable = false, length = 100)
    private String fromX;  // 시작 상태 (예: "백수" 또는 "75")

    @Column(nullable = false, length = 100)
    private String toY;   // 목표 상태 (예: "취업 성공" 또는 "68")

    @Column(nullable = false)
    private LocalDate byWhen;  // 목표 달성 기한

    @Enumerated(EnumType.STRING) //ENUM 이름 그대로("NUMERIC", "STATE")를 DB에 저장하라. 이거안쓰면 0,1,2...
    @Column(nullable = false)
    private MeasureType measureType;  // 측정 유형 (NUMERIC/STATE)

    @Column(length = 20)
    private String unit;   // 단위 (measureType이 NUMERIC일 때만 사용, 예: "kg", "원")

    /**
     * Lead Measures (선행지표) - 양방향 관계
     * cascade: WIG 삭제 시 Lead Measures도 함께 삭제
     * orphanRemoval: 컬렉션에서 제거된 Lead Measure 자동 삭제
     */
    @OneToMany(mappedBy = "wig", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // “빌더에서 값을 안 주면, 필드에 선언된 기본값을 써라”
    private List<LeadMeasure> leadMeasures = new ArrayList<>();

    /**
     * Milestones (마일스톤) - 양방향 관계
     * STATE 타입 WIG에만 사용됩니다
     */
    @OneToMany(mappedBy = "wig", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC") // orderIndex 컬럼 기준으로 오름차순 정렬
    @Builder.Default
    private List<Milestone> milestones = new ArrayList<>();

    /**
     * Commitments (주간 약속) - 양방향 관계
     * 모든 타입의 WIG에서 사용됩니다
     */
    @OneToMany(mappedBy = "wig", cascade = CascadeType.ALL, orphanRemoval = true) // Commitment의 wig필드가 FK. FK를 가진쪽이 연관관계의 주인, wig의 모든 작업을 자식에게 전파, 부모와 관계끊어지면 삭제
    @Builder.Default
    private List<Commitment> commitments = new ArrayList<>();


    @CreationTimestamp // INSERT시 자동으로 현재 시간 기록
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // UPDATE시 자동으로 현재 시간 갱신
    @Column(nullable = false)
    private LocalDateTime updatedAt;


    /**
     * 편의 메서드: Lead Measure 추가
     * 양방향 관계 동기화
     */
    public void addLeadMeasure(LeadMeasure leadMeasure) {
        leadMeasures.add(leadMeasure);
        leadMeasure.setWig(this);
    }

    /**
     * 편의 메서드: Lead Measure 제거
     * 양방향 관계 동기화
     */
    public void removeLeadMeasure(LeadMeasure leadMeasure) {
        leadMeasures.remove(leadMeasure);
        leadMeasure.setWig(null); // FK를 들고 있는쪽에서 파괴할때 orphanRemoval = true 발동
    }


    /**
     * 편의 메서드: Milestone 추가
     */
    public void addMilestone(Milestone milestone) {
        milestones.add(milestone); // 객체 그래프 연결
        milestone.setWig(this); // 연관관계의 주인 ㅡ> DB반영
    }

    /**
     * 편의 메서드: Milestone 제거
     */
    public void removeMilestone(Milestone milestone) {
        milestones.remove(milestone);
        milestone.setWig(null);
    }

    /**
     * 편의 메서드: Commitment 추가
     */
    public void addCommitment(Commitment commitment) {
        commitments.add(commitment);
        commitment.setWig(this);
    }

    /**
     * 편의 메서드: Commitment 제거
     */
    public void removeCommitment(Commitment commitment) {
        commitments.remove(commitment);
        commitment.setWig(null);
    }

    /**
     * NUMERIC 타입 WIG 생성용 팩토리 메서드
     */
    public static Wig createNumericWig(String title, String fromX, String toY,
                                   LocalDate byWhen, String unit) {

        return Wig.builder() // 빌더 : 생성자 많을 때, 무슨필드인지 가독성up. 필드누락이나 순서 실수 방지
                .title(title)
                .fromX(fromX)
                .toY(toY)
                .byWhen(byWhen)
                .measureType(MeasureType.NUMERIC)
                .unit(unit)
                .leadMeasures(new ArrayList<>())
                .milestones(new ArrayList<>())
                .commitments(new ArrayList<>())
                .build();
    }

    /**
     * STATE 타입 WIG 생성용 팩토리 메서드
     */
    public static Wig createStateWig(String title, String fromX,
                                     String toY, LocalDate byWhen) {
        return Wig.builder()
                .title(title)
                .fromX(fromX)
                .toY(toY)
                .byWhen(byWhen)
                .measureType(MeasureType.STATE)
                .leadMeasures(new ArrayList<>())
                .milestones(new ArrayList<>())
                .commitments(new ArrayList<>())
                .build();

    }
}
