package com.fdx.backend.domain.dailydata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * DailyData Repository
 */
@Repository
public interface DailyDataRepository extends JpaRepository<DailyData, Long> {

    /**
     * 특정 WIG의 모든 일간 데이터 조회 (날짜 순서대로)
     */
    List<DailyData> findByWigIdOrderByDateAsc(Long wigId);

    /**
     * 특정 WIG의 특정 주차 일간 데이터 조회
     */
    List<DailyData> findByWigIdAndWeekOrderByDateAsc(Long wigId, String week);

    /**
     * 특정 WIG의 특정 날짜 데이터 조회
     */
    Optional<DailyData> findByWigIdAndDate(Long wigId, LocalDate date);

    /**
     * 특정 WIG의 날짜 범위 데이터 조회
     */
    List<DailyData> findByWigIdAndDateBetweenOrderByDateAsc(
            Long wigId, LocalDate startDate, LocalDate endDate);

    /**
     * 특정 WIG의 특정 날짜 데이터 존재 여부
     */
    boolean existsByWigIdAndDate(Long wigId, LocalDate date);

}
