package com.fdx.backend.domain.weeklydata;

import com.fdx.backend.domain.wig.Wig;
import com.fdx.backend.domain.wig.WigRepository;
import com.fdx.backend.dto.WeeklyDataRequest;
import com.fdx.backend.dto.WeeklyDataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * WeeklyData 비즈니스 로직 처리 Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WeeklyDataService {


    private final WeeklyDataRepository weeklyDataRepository;
    private final WigRepository wigRepository;

    /**
     * 특정 WIG의 모든 주간 데이터 조회
     */
    public List<WeeklyDataResponse> getWeeklyDataByWigId(Long wigId) {
        log.info("WIG {}의 주간 데이터 조회", wigId);

        if (!wigRepository.existsById(wigId)) {
            throw new IllegalArgumentException("해당 WIG를 찾을 수 없습니다: " + wigId);
        }

        return weeklyDataRepository.findByWigIdOrderByWeekAsc(wigId).stream()
                .map(WeeklyDataResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 WIG의 특정 주차 데이터 조회
     */
    public WeeklyDataResponse getWeeklyDataByWigIdAndWeek(Long wigId, String week) {
        log.info("WIG {}의 {} 데이터 조회", wigId, week);

        WeeklyData weeklyData = weeklyDataRepository.findByWigIdAndWeek(wigId, week)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("WIG %d의 %s 데이터를 찾을 수 없습니다", wigId, week)));

        return WeeklyDataResponse.from(weeklyData);
    }

    /**
     * 주간 데이터 생성
     */
    @Transactional
    public WeeklyDataResponse createWeeklyData(WeeklyDataRequest request) {
        log.info("주간 데이터 생성: wigId={}, week={}", request.getWigId(), request.getWeek());

        // WIG 조회
        Wig wig = wigRepository.findById(request.getWigId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 WIG를 찾을 수 없습니다: " + request.getWigId()));

        // 중복 체크 (엔티티에 uniqueConstraints 해놨음)
        if (weeklyDataRepository.existsByWigIdAndWeek(request.getWigId(), request.getWeek())) {
            throw new IllegalStateException(
                    String.format("WIG %d의 %s 데이터가 이미 존재합니다",
                            request.getWigId(), request.getWeek()));
        }

        // 주간 데이터 생성
        WeeklyData weeklyData = WeeklyData.builder()
                .week(request.getWeek())
                .milestoneProgress(request.getMilestoneProgress())
                .actual(request.getActual())
                .target(request.getTarget())
                .lead1(request.getLead1())
                .lead2(request.getLead2())
                .wig(wig) // FK
                .build();

        WeeklyData savedWeeklyData = weeklyDataRepository.save(weeklyData);
        log.info("주간 데이터 생성 완료: id={}", savedWeeklyData.getId());

        return WeeklyDataResponse.from(savedWeeklyData);
    }

    /**
     * 주간 데이터 수정
     */
    @Transactional
    public WeeklyDataResponse updateWeeklyData(Long id, WeeklyDataRequest request) {
        log.info("주간 데이터 수정: id={}", id);

        WeeklyData weeklyData = weeklyDataRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 주간 데이터를 찾을 수 없습니다: " + id));

        // 엔티티 수정
        weeklyData.setWeek(request.getWeek());
        weeklyData.setMilestoneProgress(request.getMilestoneProgress());
        weeklyData.setActual(request.getActual());
        weeklyData.setTarget(request.getTarget());
        weeklyData.setLead1(request.getLead1());
        weeklyData.setLead2(request.getLead2());

        log.info("주간 데이터 수정 완료: id={}", id);

        return WeeklyDataResponse.from(weeklyData);
    }

    /**
     * 주간 데이터 삭제
     */
    @Transactional
    public void deleteWeeklyData(Long id) {
        log.info("주간 데이터 삭제: id={}", id);

        if (!weeklyDataRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 주간 데이터를 찾을 수 없습니다: " + id);
        }

        weeklyDataRepository.deleteById(id);
        log.info("주간 데이터 삭제 완료: id={}", id);
    }
}
