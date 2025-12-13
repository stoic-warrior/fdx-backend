package com.fdx.backend.domain.leadmeasure;

import com.fdx.backend.dto.LeadMeasureRequest;
import com.fdx.backend.dto.LeadMeasureResponse;
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
 * Lead Measure REST API Controller
 *
 * API 엔드포인트:
 * GET    /api/wigs/{wigId}/lead-measures     - 특정 WIG의 모든 Lead Measures 조회
 * POST   /api/lead-measures                   - Lead Measure 생성
 * PUT    /api/lead-measures/{id}              - Lead Measure 수정
 * DELETE /api/lead-measures/{id}              - Lead Measure 삭제
 */
@RestController // @RestController = @Controller + @ResponseBody
@RequiredArgsConstructor // final 필드 주입하기 위한 생성자
@Slf4j  // 로깅 객체(log)를 자동 생성 ㅡ> log.info 사용가능
public class LeadMeasureController {

    private final LeadMeasureService leadMeasureService;

    /**
     * 특정 WIG의 모든 Lead Measures 조회
     * GET /api/wigs/{wigId}/lead-measures
     */
    @GetMapping("/api/wigs/{wigId}/lead-measures")
    public ResponseEntity<List<LeadMeasureResponse>> getLeadMeasuresByWigId(
            @PathVariable Long wigId) {
        log.info("GET /api/wigs/{}/lead-measures - Lead Measures 조회 요청", wigId);
        List<LeadMeasureResponse> leadMeasures = leadMeasureService.getLeadMeasuresByWigId(wigId);
        return ResponseEntity.ok(leadMeasures);
    }


    /**
     * Lead Measure 생성
     * POST /api/lead-measures
     */
    @PostMapping("/api/lead-measures")
    public ResponseEntity<LeadMeasureResponse> createLeadMeasure(
            @Valid @RequestBody LeadMeasureRequest request) {
        log.info("POST /api/lead-measures - Lead Measure 생성 요청: {}", request.getName());
        LeadMeasureResponse createdLeadMeasure = leadMeasureService.createLeadMeasure(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLeadMeasure);
    }

    /**
     * Lead Measure 수정
     * PUT /api/lead-measures/{id}
     */
    @PutMapping("/api/lead-measures/{id}")
    public ResponseEntity<LeadMeasureResponse> updateLeadMeasure(
            @PathVariable Long id,
            @Valid @RequestBody LeadMeasureRequest request) {
        log.info("PUT /api/lead-measures/{} - Lead Measure 수정 요청", id);

        LeadMeasureResponse updatedLeadMeasure = leadMeasureService.updateLeadMeasure(id, request);
        return ResponseEntity.ok(updatedLeadMeasure);

    }

    /**
     * Lead Measure 삭제
     * DELETE /api/lead-measures/{id}
     */
    @DeleteMapping("/api/lead-measures/{id}")
    public ResponseEntity<Void>  deleteLeadMeasure(@PathVariable Long id) {
        log.info("DELETE /api/lead-measures/{} - Lead Measure 삭제 요청", id);

        leadMeasureService.deleteLeadMeasure(id);
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
