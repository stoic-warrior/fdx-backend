package com.fdx.backend.domain.leadmeasure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<List<LeadMeasureResponse>> getLeadMaesuresByWigId (
            @PathVariable Long wigId) {
        log.info("GET /api/wigs/{}/lead-measures - Lead Measures 조회 요청", wigId);
        List<LeadMeasureResponse> LeadMeasures = leadMeasureService.getLeadMeasuresByWigId(wigId);
        return ResponseEntity.ok(LeadMeasures);
    }
}
