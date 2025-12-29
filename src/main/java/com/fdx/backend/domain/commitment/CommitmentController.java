package com.fdx.backend.domain.commitment;

import com.fdx.backend.dto.CommitmentRequest;
import com.fdx.backend.dto.CommitmentResponse;
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
 * Commitment REST API Controller
 *
 * API 엔드포인트:
 * GET    /api/wigs/{wigId}/commitments                - 특정 WIG의 모든 Commitments 조회
 * GET    /api/wigs/{wigId}/commitments/week/{week}   - 특정 WIG의 특정 주차 Commitments 조회
 * GET    /api/wigs/{wigId}/commitments/week/{week}/rate - 특정 주차 이행률 조회
 * POST   /api/commitments                              - Commitment 생성
 * PUT    /api/commitments/{id}                         - Commitment 수정
 * PATCH  /api/commitments/{id}/toggle                  - Commitment 완료 상태 토글
 * DELETE /api/commitments/{id}                         - Commitment 삭제
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class CommitmentController {

    private final CommitmentService commitmentService;

    /**
     * 특정 WIG의 모든 Commitments 조회
     * GET /api/wigs/{wigId}/commitments
     */
    @GetMapping("/api/wigs/{wigId}/commitments")
    public ResponseEntity<List<CommitmentResponse>> getCommitmentsByWigId(
            @PathVariable Long wigId) {
        log.info("GET /api/wigs/{}/commitments - Commitments 조회 요청", wigId);
        List<CommitmentResponse> commitments = commitmentService.getCommitmentsByWigId(wigId);
        return ResponseEntity.ok(commitments);
    }

    /**
     * 특정 WIG의 특정 주차 Commitments 조회
     * GET /api/wigs/{wigId}/commitments/week/{week}
     */
    @GetMapping("/api/wigs/{wigId}/commitments/week/{week}")
    public ResponseEntity<List<CommitmentResponse>> getCommitmentsByWigIdAndWeek(
            @PathVariable Long wigId,
            @PathVariable String week) {
        log.info("GET /api/wigs/{}/commitments/week/{} - 주차별 Commitments 조회 요청", wigId, week);
        List<CommitmentResponse> commitments = commitmentService.getCommitmentsByWigIdAndWeek(wigId, week);
        return ResponseEntity.ok(commitments);
    }

    /**
     * 특정 WIG의 특정 주차 이행률 조회
     * GET /api/wigs/{wigId}/commitments/week/{week}/rate
     *
     * 응답 예시:
     * {
     *   "wigId": 1,
     *   "week": "W5",
     *   "total": 4,
     *   "completed": 2,
     *   "completionRate": 50.0
     * }
     */
    @GetMapping("/api/wigs/{wigId}/commitments/week/{week}/rate")
    public ResponseEntity<Map<String,Object>> getCommitmentCompletionRate(
            @PathVariable Long wigId,
            @PathVariable String week) {
        log.info("GET /api/wigs/{}/commitments/week/{}/rate - 이행률 조회 요청", wigId, week);
        Map<String, Object> rate = commitmentService.getCommitmentCompletionRate(wigId, week);
        return ResponseEntity.ok(rate);
    }

    /**
     * Commitment 생성
     * POST /api/commitments
     */
    @PostMapping("/api/commitments")
    public ResponseEntity<CommitmentResponse> createCommitment(
            @Valid @RequestBody CommitmentRequest request) {
        log.info("POST /api/commitments - Commitment 생성 요청: {}", request.getText());
        CommitmentResponse createdCommitment = commitmentService.createCommitment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCommitment);
    }

    /**
     * Commitment 수정
     * PUT /api/commitments/{id}
     */
    @PutMapping("/api/commitments/{id}")
    public ResponseEntity<CommitmentResponse> updateCommitment(
            @PathVariable Long id,
            @Valid @RequestBody CommitmentRequest request) {
        log.info("PUT /api/commitments/{} - Commitment 수정 요청", id);
        CommitmentResponse updatedCommitment = commitmentService.updateCommitment(id, request);
        return ResponseEntity.ok(updatedCommitment);
    }

    /**
     * Commitment 완료 상태 토글
     * PATCH /api/commitments/{id}/toggle
     *
     * 체크박스 클릭 시 사용
     */
    @PatchMapping("/api/commitments/{id}/toggle") // Patch = 일부만수정. 안보낸건 그대로 유지
    public ResponseEntity<CommitmentResponse> toggleCommitmentCompleted(
            @PathVariable Long id) {
        log.info("PATCH /api/commitments/{}/toggle - Commitment 완료 상태 토글 요청", id);
        CommitmentResponse commitment = commitmentService.toggleCommitmentCompleted(id);
        return ResponseEntity.ok(commitment);
    }

    /**
     * Commitment 삭제
     * DELETE /api/commitments/{id}
     */
    @DeleteMapping("/api/commitments/{id}")
    public ResponseEntity<Void> deleteCommitment(@PathVariable Long id) {
        log.info("DELETE /api/commitments/{} - Commitment 삭제 요청", id);
        commitmentService.deleteCommitment(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 예외 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(
            IllegalArgumentException e) {
        log.error("요청 처리 중 오류 발생: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
