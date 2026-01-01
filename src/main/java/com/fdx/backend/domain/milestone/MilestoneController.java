package com.fdx.backend.domain.milestone;

import com.fdx.backend.dto.MilestoneRequest;
import com.fdx.backend.dto.MilestoneResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Milestone REST API Controller
 *
 * API 엔드포인트:
 * GET    /api/wigs/{wigId}/milestones           - 특정 WIG의 모든 Milestones 조회
 * GET    /api/wigs/{wigId}/milestones/progress  - 특정 WIG의 Milestone 진행률 조회
 * POST   /api/milestones                         - Milestone 생성
 * PUT    /api/milestones/{id}                    - Milestone 수정
 * PATCH  /api/milestones/{id}/toggle             - Milestone 완료 상태 토글
 * DELETE /api/milestones/{id}                    - Milestone 삭제
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class MilestoneController {

    private final MilestoneService milestoneService;

    /**
     * 특정 WIG의 모든 Milestones 조회
     * GET /api/wigs/{wigId}/milestones
     */
    @GetMapping("/api/wigs/{wigId}/milestones")
    public ResponseEntity<List<MilestoneResponse>> getMilestonesByWigId(
            @PathVariable Long wigId) {
        log.info("GET /api/wigs/{}/milestones - Milestones 조회 요청", wigId);
        List<MilestoneResponse> milestones = milestoneService.getMilestonesByWigId(wigId);
        return ResponseEntity.ok(milestones);
    }

    /**
     * 특정 WIG의 Milestone 진행률 조회
     * GET /api/wigs/{wigId}/milestones/progress
     *
     * 응답 예시:
     * {
     *   "total": 5,
     *   "completed": 2,
     *   "progressRate": 40.0
     * }
     */
    @GetMapping("/api/wigs/{wigId}/milestones/progress")
    public ResponseEntity<Map<String, Object>> getMilestoneProgress(
            @PathVariable Long wigId) {
        log.info("GET /api/wigs/{}/milestones/progress - Milestone 진행률 조회 요청", wigId);
        Map<String, Object> progress = milestoneService.getMilestoneProgress(wigId);
        return ResponseEntity.ok(progress);
    }

    /**
     * Milestone 생성
     * POST /api/milestones
     */
    @PostMapping("/api/milestones")
    public ResponseEntity<MilestoneResponse> createMilestone(
            @Valid @RequestBody MilestoneRequest request) {
        log.info("POST /api/milestones - Milestone 생성 요청: {}", request.getName());
        MilestoneResponse createdMilestone = milestoneService.createMilestone(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMilestone);
    }

    /**
     * Milestone 수정
     * PUT /api/milestones/{id}
     */
    @PutMapping("/api/milestones/{id}")
    public ResponseEntity<MilestoneResponse> updateMilestone(
            @PathVariable Long id,
            @Valid @RequestBody MilestoneRequest request) {
        log.info("PUT /api/milestones/{} - Milestone 수정 요청", id);
        MilestoneResponse updatedMilestone = milestoneService.updateMilestone(id, request);
        return ResponseEntity.ok(updatedMilestone);
    }

    /**
     * Milestone 완료 상태 토글
     * PATCH /api/milestones/{id}/toggle
     *
     * 완료 <-> 미완료 상태를 간편하게 전환
     */
    @PatchMapping("/api/milestones/{id}/toggle") // put = 덮어쓰기, patch = 부분수정
    public ResponseEntity<MilestoneResponse> toggleMilestoneCompleted(
            @PathVariable Long id) {
        log.info("PATCH /api/milestones/{}/toggle - Milestone 완료 상태 토글 요청", id);
        MilestoneResponse milestone = milestoneService.toggleMilestoneCompleted(id);
        return ResponseEntity.ok(milestone);
    }

    /**
     * Milestone 삭제
     * DELETE /api/milestones/{id}
     */
    @DeleteMapping("/api/milestones/{id}")
    public ResponseEntity<Void> deleteMilestone(@PathVariable Long id) {
        log.info("DELETE /api/milestones/{} - Milestone 삭제 요청", id);
        milestoneService.deleteMilestone(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 예외 처리
     */
    @ExceptionHandler(IllegalArgumentException.class) // IllegalArgumentException이 던져지면 이 메서드가 출동한다
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(
            IllegalArgumentException e) {
        log.error("요청 처리 중 오류 발생: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

}
