package com.fdx.backend.domain.leadmeasure;

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
        log.info("Lead Measure 생성: name={}, wigId={}", request.getName(), request.getWigId());

        // WIG조회
        Wig wig = wigRepository.findById(request.getWigId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 WIG를 찾을 수 없습니다: " + request.getWigId()));

        // Lead Measure 생성
        LeadMeasure leadMeasure = LeadMeasure.builder()
                .name(request.getName())
                .dailyTarget(request.getDailyTarget())
                .weeklyTarget(request.getWeeklyTarget())
                .unit(request.getUnit())
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
        leadMeasure.setDailyTarget(request.getDailyTarget());
        leadMeasure.setWeeklyTarget(request.getWeeklyTarget());
        leadMeasure.setUnit(request.getUnit());

        log.info("Lead Measure 수정 완료: id={}", id);

        return LeadMeasureResponse.from(leadMeasure);

    }


    /**
     * Lead Measure 삭제
     */
    @Transactional
    public void deleteLeadMeasure(Long id) {
        log.info("Lead Measure 삭제: id={}", id);

        if (!leadMeasureRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 Lead Measure를 찾을 수 없습니다: " + id);
        }

        leadMeasureRepository.deleteById(id);
        log.info("Lead Measure 삭제 완료: id={}", id);

    }

}
