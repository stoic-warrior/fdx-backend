package com.fdx.backend.domain.milestone;

import com.fdx.backend.domain.wig.Wig;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Milestone (마일스톤) 엔티티
 * STATE 타입 WIG의 진행 단계를 나타냅니다
 *
 * 예시:
 * - "백엔드 개발자 취업" WIG의 마일스톤:
 *   1. 포트폴리오 3개 완성 ✓
 *   2. Spring Boot 프로젝트 완성 ✓
 *   3. 이력서 10곳 제출
 *   4. 면접 5회 진행
 *   5. 최종 합격
 */
@Entity
@Table(name = "milestones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // 빌더 메소드를 장착, 서비스에서 메소드 사용
public class Milestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /**
     * 마일스톤 이름
     * 예: "포트폴리오 3개 완성", "면접 5회 진행"
     */
    @Column(nullable = false, length = 200)
    private String name;

    /**
     * 완료 여부
     */
    @Column(nullable = false)
    @Builder.Default // 빌더로 생성할때 생략한 필드는, 빌더가 필드 초기값(false)를 무시하고 null을 쓸 수 있다. 그러지말고 필드 초기값 쓰라는게 이 어노테이션
    private Boolean completed = false;

    /**
     * 순서 (1부터 시작)
     * 마일스톤의 진행 순서를 나타냄
     */
    @Column(nullable = false)
    private Integer orderIndex;

    /**
     * 소속 WIG (Many-to-One)
     * STATE 타입 WIG만 마일스톤을 가집니다
     */
    @ManyToOne(fetch = FetchType.LAZY) // “마일스톤 N개 : WIG 1개"
    // LAZY(지연 로딩): Milestone을 조회할 때 wig를 즉시 DB에서 같이 안 가져온다.
    // 처음엔 wig 자리에 “프록시(대리객체)”만 꽂아둠
    // 진짜 milestone.getWig()를 호출하는 순간, 그때 DB를 한 번 더 쳐서 wig를 로딩
    @JoinColumn(name = "wig_id", nullable = false)
    private Wig wig;

    @CreationTimestamp // INSERT 시 자동으로 timestamp 입력.
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // update 시 자동으로 timestamp 입력.
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 편의 메서드: 이 Milestone의 WIG 설정
     * 양방향 관계 동기화
     */
    public void setWig(Wig wig) {
        this.wig = wig;
        if (wig != null && !wig.getMilestones().contains(this)) {
            wig.getMilestones().add(this);
        }
    }


    /**
     * 편의 메서드: 마일스톤 완료 토글
     */
    public void toggleCompleted() {
        this.completed = !this.completed;
    }

}
