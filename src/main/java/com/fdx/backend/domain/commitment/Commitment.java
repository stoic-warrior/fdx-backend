package com.fdx.backend.domain.commitment;

import com.fdx.backend.domain.wig.Wig;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Commitment (주간 약속) 엔티티
 * 4DX Discipline 4: 책임의 리듬 만들기
 *
 * 매주 WIG 달성을 위한 구체적인 약속을 세우고 실천합니다
 *
 * 예시:
 * - "매일 아침 30분 조깅"
 * - "백엔드 로드맵 정리"
 * - "코딩테스트 3문제 풀기"
 */
@Entity
@Table(name = "commitments") // 매핑될 테이블명
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commitment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 약속 내용
     */
    @Column(nullable = false, length = 300)
    private String text;

    /**
     * 주차 (예: "W1", "W2", "W5")
     * 어느 주에 세운 약속인지 표시
     */
    @Column(nullable = false, length = 10)
    private String week;

    /**
     * 완료 여부
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean completed = false;

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

    /**
     * 편의 메서드: 이 Commitment의 WIG 설정
     * 양방향 관계 동기화
     */
    public void setWig(Wig wig) {
        this.wig = wig;
        if (wig != null && !wig.getCommitments().contains(this)) {
            wig.getCommitments().add(this);
        }

    }

    /**
     * 편의 메서드: 완료 상태 토글
     */
    public void toggleCompleted() {
        this.completed = !this.completed;
    }
}
