package com.fdx.backend.domain.wig;

import com.fdx.backend.dto.WigRequest;
import com.fdx.backend.dto.WigResponse;
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
 * WIG REST API Controller
 *
 * API 엔드포인트:
 * GET    /api/wigs           - 모든 WIG 조회
 * GET    /api/wigs/count     - WIG 개수 조회
 * POST   /api/wigs           - WIG 생성 (최대 2개)
 * PUT    /api/wigs/{id}      - WIG 수정
 * DELETE /api/wigs/{id}      - WIG 삭제
 */
@RestController
@RequestMapping("/api/wigs")
@RequiredArgsConstructor
@Slf4j
public class WigController {

    private final WigService wigService;

    /**
     * 모든 WIG 조회
     * GET /api/wigs
     */
    @GetMapping
    public ResponseEntity<List<WigResponse>> getAllWigs() {
        log.info("GET /api/wigs - 모든 WIG 조회 요청");
        List<WigResponse> wigs = wigService.getAllWigs();
        return ResponseEntity.ok(wigs);
    }

    /**
     * WIG 개수 조회 (프론트엔드에서 "더 추가 가능한지" 확인용)
     * GET /api/wigs/count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getWigCount() {
        log.info("GET /api/wigs/count - WIG 개수 조회 요청");
        long count = wigService.countWigs(); // jpa에서 count는 long타입 반환

        Map<String,Object> response = new HashMap<>();
        response.put("count",count);
        response.put("maxCount",2);
        response.put("canAddMore",count<2);

        return ResponseEntity.ok(response);
    }

    /**
     * WIG 생성 (최대 2개 제한)
     * POST /api/wigs
     */
    @PostMapping
    public ResponseEntity<WigResponse> createWig(@Valid @RequestBody WigRequest request) {
        log.info("POST /api/wigs - WIG 생성 요청: {}", request.getTitle());
        WigResponse createdWig = wigService.createWig(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWig);
    }

    /**
     * WIG 수정
     * PUT /api/wigs/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<WigResponse> updateWig(
            @PathVariable Long id,
            @Valid @RequestBody WigRequest request) {

        log.info("PUT /api/wigs/{} - WIG 수정 요청",id);
        WigResponse updatedWig = wigService.updateWig(id,request);
        return ResponseEntity.ok(updatedWig);
    }

    /**
     * WIG 삭제
     * DELETE /api/wigs/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWig(@PathVariable Long id) {
        log.info("DELETE /api/wigs/{} - WIG 삭제 요청",id);
        wigService.deleteWig(id);
        return ResponseEntity.noContent().build();
    }


    /**
     * 예외 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("요청 처리 중 오류 발생: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException e) {
        log.error("상태 오류: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
