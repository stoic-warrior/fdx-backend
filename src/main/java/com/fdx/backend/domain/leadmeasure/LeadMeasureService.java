package com.fdx.backend.domain.leadmeasure;

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
    public LeadMeasureResponse createLeadMeasure(LeadMeasureRequest leadMeasureRequest) {

    }
}
