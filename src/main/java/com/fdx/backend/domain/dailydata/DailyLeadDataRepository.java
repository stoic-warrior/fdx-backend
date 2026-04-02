package com.fdx.backend.domain.dailydata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DailyLeadDataRepository extends JpaRepository<DailyLeadData, Long> {

    List<DailyLeadData> findByDailyDataId(Long dailyDataId);

    Optional<DailyLeadData> findByDailyDataIdAndLeadMeasureId(Long dailyDataId, Long leadMeasureId);

    void deleteByDailyDataId(Long dailyDataId);

    void deleteByLeadMeasureId(Long leadMeasureId);
}
