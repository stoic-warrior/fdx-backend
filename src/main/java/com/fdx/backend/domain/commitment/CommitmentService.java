package com.fdx.backend.domain.commitment;

import com.fdx.backend.domain.wig.WigRepository;
import com.fdx.backend.dto.CommitmentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Commitment 비즈니스 로직 처리 Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CommitmentService {

    private final CommitmentRepository commitmentRepository;
    private final WigRepository wigRepository;

    /**
     * 특정 WIG의 모든 Commitments 조회
     */
    public List<CommitmentResponse> getCommitmentsByWigId(Long wigId) {
        log.info("WIG {}의 Commitments 조회", wigId);

        if(!wigRepository.existsById(wigId)) {
            throw new IllegalArgumentException("해당 WIG를 찾을 수 없습니다: " + wigId);
        }

        return commitmentRepository.findByWigId(wigId).stream()
                .map(CommitmentResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 WIG의 특정 주차 Commitments 조회
     */
    public List<CommitmentResponse> getCommitmentsByWigIdAndWeek(Long wigId, String week) {
        log.info("WIG {}의 {} Commitments 조회", wigId, week);

        if(!wigRepository.existsById(wigId)) {
            throw new IllegalArgumentException("해당 WIG를 찾을 수 없습니다: " + wigId);
        }

        return commitmentRepository.findByWigIdAndWeek(wigId, week).stream()
                .map(CommitmentResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 WIG의 특정 주차 이행률 조회
     */
    public Map<String, Object> getCommitmentCompletionRate(Long wigId, String week) {

    }


}
