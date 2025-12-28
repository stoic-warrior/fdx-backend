package com.fdx.backend.domain.commitment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Commitment Repository
 */
@Repository
public interface CommitmentRepository extends JpaRepository<Commitment, Long> {

    /**
     * 특정 WIG의 모든 Commitments 조회
     */
    List<Commitment> findByWigId(Long wigId);

    /**
     * 특정 WIG의 특정 주차 Commitments 조회
     */
    List<Commitment> findByWigIdAndWeek(Long wigId, String week);

    /**
     * 특정 주차의 모든 Commitments 조회
     */
    List<Commitment> findByWeek(String week);

    /**
     * 특정 WIG의 특정 주차 완료된 Commitments 개수
     */
    long countByWigIdAndWeekAndCompletedTrue(Long wigId, String week);

    /**
     * 특정 WIG의 특정 주차 전체 Commitments 개수
     */
    long countByWigIdAndWeek(Long wigId, String week);

}
