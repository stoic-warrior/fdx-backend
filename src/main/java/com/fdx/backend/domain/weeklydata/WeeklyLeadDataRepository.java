package com.fdx.backend.domain.weeklydata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyLeadDataRepository extends JpaRepository<WeeklyLeadData, Long> {

    List<WeeklyLeadData> findByWeeklyDataId(Long weeklyDataId);

    Optional<WeeklyLeadData> findByWeeklyDataIdAndLeadMeasureId(Long weeklyDataId, Long leadMeasureId);

    void deleteByWeeklyDataId(Long weeklyDataId);

    void deleteByLeadMeasureId(Long leadMeasureId);
}
