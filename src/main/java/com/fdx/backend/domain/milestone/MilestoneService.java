package com.fdx.backend.domain.milestone;

import com.fdx.backend.domain.MeasureType;
import com.fdx.backend.domain.wig.Wig;
import com.fdx.backend.domain.wig.WigRepository;
import com.fdx.backend.dto.MilestoneRequest;
import com.fdx.backend.dto.MilestoneResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Milestone 비즈니스 로직 처리 Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final WigRepository wigRepository;

    /**
     * 특정 WIG의 모든 Milestones 조회
     */
    public List<MilestoneResponse> getMilestonesByWigId(Long wigId) {
        log.info("WIG {}의 Milestones 조회", wigId);

        // WIG 존재 여부 확인
        if (!wigRepository.existsById(wigId)) {
            throw new IllegalArgumentException("해당 WIG를 찾을 수 없습니다: " + wigId);
        }

        return milestoneRepository.findByWigIdOrderByOrderIndexAsc(wigId).stream()
                .map(MilestoneResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 WIG의 진행률 조회
     */
    public Map<String, Object> getMilestoneProgress(Long wigId) {
        log.info("WIG {}의 Milestone 진행률 조회", wigId);

        // WIG 존재 여부 및 타입 확인
        Wig wig = wigRepository.findById(wigId)
                .orElseThrow(() -> new IllegalArgumentException("해당 WIG를 찾을 수 없습니다: " + wigId));

        if (wig.getMeasureType() != MeasureType.STATE) {
            throw new IllegalArgumentException("STATE 타입 WIG만 Milestone을 가질 수 있습니다");
        }

        long total = milestoneRepository.countByWigId(wigId);
        long completed = milestoneRepository.countByWigIdAndCompletedTrue(wigId);
        double progressRate = total > 0 ? (completed * 100.0 / total) : 0.0;

        Map<String, Object> progress = new HashMap<>();
        progress.put("total", total);
        progress.put("completed", completed);
        progress.put("progressRate", Math.round(progressRate * 10) / 10.0); // 소수점 1자리

        return progress;
    }

    /**
     * Milestone 생성
     */
    @Transactional
    public MilestoneResponse createMilestone(MilestoneRequest request) {
        log.info("Milestone 생성: name={}, wigId={}", request.getName(), request.getWigId());

        // WIG 조회 및 타입 검증
        Wig wig = wigRepository.findById(request.getWigId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 WIG를 찾을 수 없습니다: " + request.getWigId()));

        if (wig.getMeasureType() != MeasureType.STATE) {
            throw new IllegalArgumentException("STATE 타입 WIG만 Milestone을 가질 수 있습니다");
        }

        // Milestone 생성
        Milestone milestone = Milestone.builder()
                .name(request.getName())
                .completed(request.getCompleted() != null ? request.getCompleted() : false) // DTO에서 notnull을 안하고 null을 허용하니 이게 필요.  엔티티에 @Builder.Default가 있지만 지금처럼 명시적으로 null을 주입할땐 작동안한다
                .orderIndex(request.getOrderIndex())
                .wig(wig)
                .build();

        // 양방향 관계 동기화
        wig.addMilestone(milestone);

        Milestone savedMilestone = milestoneRepository.save(milestone);
        log.info("Milestone 생성 완료: id={}", savedMilestone.getId());

        return MilestoneResponse.from(savedMilestone);
    }

}
