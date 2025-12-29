package com.fdx.backend.domain.commitment;

import com.fdx.backend.domain.wig.Wig;
import com.fdx.backend.domain.wig.WigRepository;
import com.fdx.backend.dto.CommitmentRequest;
import com.fdx.backend.dto.CommitmentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
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

        if (!wigRepository.existsById(wigId)) {
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

        if (!wigRepository.existsById(wigId)) {
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
        log.info("WIG {}의 {} 이행률 조회", wigId, week);

        if (!wigRepository.existsById(wigId)) {
            throw new IllegalArgumentException("해당 WIG를 찾을 수 없습니다: " + wigId);
        }

        long total = commitmentRepository.countByWigIdAndWeek(wigId, week);
        long completed = commitmentRepository.countByWigIdAndWeekAndCompletedTrue(wigId, week);
        double completionRate = total > 0 ? (completed * 100.0 / total) : 0.0;

        Map<String, Object> result = new HashMap<>();
        result.put("wigId", wigId);
        result.put("week", week);
        result.put("total", total);
        result.put("completed", completed);
        result.put("completionRate", Math.round(completionRate * 10) / 10.0); // 소수 1자리로 반올림

        return result;
    }

    /**
     * Commitment 생성
     */
    @Transactional
    public CommitmentResponse createCommitment(CommitmentRequest request) {
        log.info("Commitment 생성: text={}, week={}, wigId={}",
                request.getText(), request.getWeek(), request.getWigId());

        // WIG 조회
        Wig wig = wigRepository.findById(request.getWigId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 WIG를 찾을 수 없습니다: " + request.getWigId()));

        // Commitment 생성
        Commitment commitment = Commitment.builder()
                .text(request.getText())
                .week(request.getWeek())
                .completed(request.getCompleted() != null ? request.getCompleted() : false)
                .wig(wig)
                .build();

        // 양방향 관계 동기화
        wig.addCommitment(commitment);

        Commitment savedCommitment = commitmentRepository.save(commitment);
        log.info("Commitment 생성 완료: id={}", savedCommitment.getId());

        return CommitmentResponse.from(savedCommitment);
    }

    /**
     * Commitment 수정
     */
    @Transactional
    public CommitmentResponse updateCommitment(Long id, CommitmentRequest request) {
        log.info("Commitment 수정: id={}", id);

        Commitment commitment = commitmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 Commitment를 찾을 수 없습니다: " + id));

        // 엔티티 수정
        commitment.setText(request.getText());
        commitment.setWeek(request.getWeek());
        if(request.getCompleted() != null) {
            commitment.setCompleted(request.getCompleted());
        }

        log.info("Commitment 수정 완료: id={}", id);

        return CommitmentResponse.from(commitment);
    }

    /**
     * Commitment 완료 상태 토글
     */
    @Transactional
    public CommitmentResponse toggleCommitmentCompleted(Long id) {
        log.info("Commitment 완료 상태 토글: id={}", id);

        Commitment commitment = commitmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 Commitment를 찾을 수 없습니다: " + id));

        commitment.toggleCompleted();

        log.info("Commitment 완료 상태 변경: id={}, completed={}", id, commitment.getCompleted());

        return CommitmentResponse.from(commitment);
    }

    /**
     * Commitment 삭제
     */
    @Transactional
    public void deleteCommitment(Long id) {
        log.info("Commitment 삭제: id={}", id);

        if (!commitmentRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 Commitment를 찾을 수 없습니다: " + id);
        }

        commitmentRepository.deleteById(id);
        log.info("Commitment 삭제 완료: id={}", id);
    }

}
