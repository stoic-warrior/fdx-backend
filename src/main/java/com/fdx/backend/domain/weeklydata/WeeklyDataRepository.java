package com.fdx.backend.domain.weeklydata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * WeeklyData Repository
 */
@Repository
public interface WeeklyDataRepository extends JpaRepository<WeeklyData, Long> {

    /**
     * 특정 WIG의 모든 주간 데이터 조회 (주차 순서대로)
     */
    List<WeeklyData> findByWigIdOrderByWeekAsc(Long wigId);

    /**
     * 특정 WIG의 특정 주차 데이터 조회
     */
    Optional<WeeklyData> findByWigIdAndWeek(Long wigId, String week);

    /**
     * 특정 WIG의 주간 데이터 존재 여부
     */
    boolean existsByWigIdAndWeek(Long wigId, String week);
}
