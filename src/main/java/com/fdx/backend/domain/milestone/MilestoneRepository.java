package com.fdx.backend.domain.milestone;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Milestone Repository
 */
@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

    /**
     * 특정 WIG의 모든 Milestones 조회 (순서대로 정렬)
     */
    List<Milestone> findByWigIdOrderByOrderIndexAsc(Long wigId);

    /**
     * 특정 WIG의 완료된 Milestones 개수
     */
    long countByWigIdAndCompletedTrue(Long wigId); // count SQL은 NULL이 안나오니 Long말고 long씀

    /**
     * 특정 WIG의 전체 Milestones 개수
     */
    long countByWigId(Long wigId);

}
