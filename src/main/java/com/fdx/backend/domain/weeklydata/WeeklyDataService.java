package com.fdx.backend.domain.weeklydata;

import com.fdx.backend.domain.leadmeasure.LeadMeasure;
import com.fdx.backend.domain.leadmeasure.LeadMeasureRepository;
import com.fdx.backend.domain.wig.Wig;
import com.fdx.backend.domain.wig.WigRepository;
import com.fdx.backend.dto.WeeklyDataRequest;
import com.fdx.backend.dto.WeeklyDataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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
    private final LeadMeasureRepository leadMeasureRepository;

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

        // LeadMeasure 엔티티 조회 (ID 검증)
        Map<Long, LeadMeasure> leadMeasureMap = leadMeasureRepository.findByWigId(request.getWigId())
                .stream().collect(Collectors.toMap(LeadMeasure::getId, lm -> lm));

        // 주간 데이터 생성
        WeeklyData weeklyData = WeeklyData.builder()
                .week(request.getWeek())
                .milestoneProgress(request.getMilestoneProgress())
                .actual(request.getActual())
                .target(request.getTarget())
                .wig(wig)
                .build();

        // leadValues 매핑
        if (request.getLeadValues() != null) {
            for (Map.Entry<Long, Double> entry : request.getLeadValues().entrySet()) {
                LeadMeasure lm = leadMeasureMap.get(entry.getKey());
                if (lm == null) {
                    throw new IllegalArgumentException("해당 Lead Measure를 찾을 수 없습니다: " + entry.getKey());
                }
                WeeklyLeadData wld = WeeklyLeadData.builder()
                        .weeklyData(weeklyData)
                        .leadMeasure(lm)
                        .value(entry.getValue())
                        .build();
                weeklyData.getLeadValues().add(wld);
            }
        }

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

        // 기본 필드 수정
        weeklyData.setWeek(request.getWeek());
        weeklyData.setMilestoneProgress(request.getMilestoneProgress());
        weeklyData.setActual(request.getActual());
        weeklyData.setTarget(request.getTarget());

        // LeadMeasure 엔티티 조회
        Map<Long, LeadMeasure> leadMeasureMap = leadMeasureRepository.findByWigId(weeklyData.getWig().getId())
                .stream().collect(Collectors.toMap(LeadMeasure::getId, lm -> lm));

        // 기존 leadValues를 교체 (orphanRemoval=true로 기존 것은 자동 삭제)
        weeklyData.getLeadValues().clear();
        if (request.getLeadValues() != null) {
            for (Map.Entry<Long, Double> entry : request.getLeadValues().entrySet()) {
                LeadMeasure lm = leadMeasureMap.get(entry.getKey());
                if (lm == null) {
                    throw new IllegalArgumentException("해당 Lead Measure를 찾을 수 없습니다: " + entry.getKey());
                }
                WeeklyLeadData wld = WeeklyLeadData.builder()
                        .weeklyData(weeklyData)
                        .leadMeasure(lm)
                        .value(entry.getValue())
                        .build();
                weeklyData.getLeadValues().add(wld);
            }
        }

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
