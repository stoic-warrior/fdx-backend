package com.fdx.backend.domain.leadmeasure;

import com.fdx.backend.domain.GoalDirection;
import com.fdx.backend.domain.LeadMeasureType;
import com.fdx.backend.domain.dailydata.DailyLeadDataRepository;
import com.fdx.backend.domain.weeklydata.WeeklyLeadDataRepository;
import com.fdx.backend.domain.wig.Wig;
import com.fdx.backend.domain.wig.WigRepository;
import com.fdx.backend.dto.LeadMeasureRequest;
import com.fdx.backend.dto.LeadMeasureResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Lead Measure 비즈니스 로직 처리 Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LeadMeasureService {

    private final LeadMeasureRepository leadMeasureRepository;
    private final WigRepository wigRepository;
    private final DailyLeadDataRepository dailyLeadDataRepository;
    private final WeeklyLeadDataRepository weeklyLeadDataRepository;
    private static final int MAX_LEAD_MEASURE_COUNT = 5; // Lead Measure 최대 갯수 제한

    /**
     * 특정 WIG의 모든 Lead Measures 조회
     */
    public List<LeadMeasureResponse> getLeadMeasuresByWigId(Long wigId) {
        log.info("WIG {}의 Lead Measures 조회", wigId);

        // WIG 존재 여부 확인
        if (!wigRepository.existsById(wigId)) {
            throw new IllegalArgumentException("해당 WIG를 찾을 수 없습니다: " + wigId);
        }

        return leadMeasureRepository.findByWigId(wigId).stream()
                .map(LeadMeasureResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Lead Measure 생성
     */
    @Transactional
    public LeadMeasureResponse createLeadMeasure(LeadMeasureRequest request) {
        log.info("Lead Measure 생성: name={}, wigId={}, goalDirection={}",
                request.getName(), request.getWigId(), request.getGoalDirection());

        // WIG조회
        Wig wig = wigRepository.findById(request.getWigId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 WIG를 찾을 수 없습니다: " + request.getWigId()));

        // Lead Measure 갯수 제한 검증
        long currentCount = leadMeasureRepository.countByWigId(request.getWigId());
        if (currentCount >= MAX_LEAD_MEASURE_COUNT) {
            throw new IllegalStateException(
                    String.format("Lead Measure는 WIG당 최대 %d개까지만 생성할 수 있습니다. (현재: %d개)",
                            MAX_LEAD_MEASURE_COUNT, currentCount));
        }

        // BOOLEAN 타입이면 기본값 강제 설정
        LeadMeasureType type = request.getLeadMeasureType() != null
                ? request.getLeadMeasureType()
                : LeadMeasureType.NUMERIC;

        Double dailyTarget = request.getDailyTarget();
        Double weeklyTarget = request.getWeeklyTarget();
        String unit = request.getUnit();
        GoalDirection direction = request.getGoalDirection() != null
                ? request.getGoalDirection()
                : GoalDirection.MAXIMIZE;

        if (type == LeadMeasureType.BOOLEAN) {
            dailyTarget = 1.0;
            weeklyTarget = 7.0;
            unit = "회";
            direction = GoalDirection.MAXIMIZE;
        }

        // Lead Measure 생성
        LeadMeasure leadMeasure = LeadMeasure.builder()
                .name(request.getName())
                .dailyTarget(dailyTarget)
                .weeklyTarget(weeklyTarget)
                .unit(unit)
                .goalDirection(direction)
                .leadMeasureType(type)
                .wig(wig)
                .build();

        // 양방향 관계 동기화
        wig.addLeadMeasure(leadMeasure);

        LeadMeasure savedLeadMeasure = leadMeasureRepository.save(leadMeasure);
        log.info("Lead Measure 생성 완료: id={}", savedLeadMeasure.getId());

        return LeadMeasureResponse.from(savedLeadMeasure);

    }

    // 트랜잭션 열고, id로 리드매셔 엔티티 받고, set으로 수정하고, dto로 감싸서 반환
    /**
     * Lead Measure 수정
     */
    @Transactional
    public LeadMeasureResponse updateLeadMeasure(Long id, LeadMeasureRequest request) {
        log.info("Lead Measure 수정: id={}", id);

        LeadMeasure leadMeasure = leadMeasureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 Lead Measure를 찾을 수 없습니다: " + id));

        // 엔티티 수정
        leadMeasure.setName(request.getName());
        if (request.getLeadMeasureType() != null) {
            leadMeasure.setLeadMeasureType(request.getLeadMeasureType());
        }

        // BOOLEAN 타입이면 기본값 강제 설정
        if (leadMeasure.getLeadMeasureType() == LeadMeasureType.BOOLEAN) {
            leadMeasure.setDailyTarget(1.0);
            leadMeasure.setWeeklyTarget(7.0);
            leadMeasure.setUnit("회");
            leadMeasure.setGoalDirection(GoalDirection.MAXIMIZE);
        } else {
            leadMeasure.setDailyTarget(request.getDailyTarget());
            leadMeasure.setWeeklyTarget(request.getWeeklyTarget());
            leadMeasure.setUnit(request.getUnit());
            if (request.getGoalDirection() != null) {
                leadMeasure.setGoalDirection(request.getGoalDirection());
            }
        }


        log.info("Lead Measure 수정 완료: id={}", id);

        return LeadMeasureResponse.from(leadMeasure);

    }


    /**
     * Lead Measure 삭제
     * 연관된 DailyLeadData, WeeklyLeadData도 함께 삭제
     */
    @Transactional
    public void deleteLeadMeasure(Long id) {
        log.info("Lead Measure 삭제: id={}", id);

        if (!leadMeasureRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 Lead Measure를 찾을 수 없습니다: " + id);
        }

        // 연관된 DailyLeadData, WeeklyLeadData 먼저 삭제
        dailyLeadDataRepository.deleteByLeadMeasureId(id);
        weeklyLeadDataRepository.deleteByLeadMeasureId(id);

        leadMeasureRepository.deleteById(id);
        log.info("Lead Measure 삭제 완료: id={}", id);

    }

}
