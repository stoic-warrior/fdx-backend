package com.fdx.backend.domain.weeklydata;

import com.fdx.backend.dto.WeeklyDataRequest;
import com.fdx.backend.dto.WeeklyDataResponse;
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
 * WeeklyData REST API Controller
 *
 * API 엔드포인트:
 * GET    /api/wigs/{wigId}/weekly-data             - 특정 WIG의 모든 주간 데이터 조회
 * GET    /api/wigs/{wigId}/weekly-data/{week}      - 특정 WIG의 특정 주차 데이터 조회
 * POST   /api/weekly-data                           - 주간 데이터 생성
 * PUT    /api/weekly-data/{id}                      - 주간 데이터 수정
 * DELETE /api/weekly-data/{id}                      - 주간 데이터 삭제
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class WeeklyDataController {

    private final WeeklyDataService weeklyDataService;

    /**
     * 특정 WIG의 모든 주간 데이터 조회
     * GET /api/wigs/{wigId}/weekly-data
     */
    @GetMapping("/api/wigs/{wigId}/weekly-data")
    public ResponseEntity<List<WeeklyDataResponse>> getWeeklyDataByWigId(
            @PathVariable Long wigId) {
        log.info("GET /api/wigs/{}/weekly-data - 주간 데이터 조회 요청", wigId);
        List<WeeklyDataResponse> weeklyData = weeklyDataService.getWeeklyDataByWigId(wigId);
        return ResponseEntity.ok(weeklyData);
    }

    /**
     * 특정 WIG의 특정 주차 데이터 조회
     * GET /api/wigs/{wigId}/weekly-data/{week}
     */
    @GetMapping("/api/wigs/{wigId}/weekly-data/{week}")
    public ResponseEntity<WeeklyDataResponse> getWeeklyDataByWigIdAndWeek(
            @PathVariable Long wigId,
            @PathVariable String week) {
        log.info("GET /api/wigs/{}/weekly-data/{} - 특정 주차 데이터 조회 요청", wigId, week);
        WeeklyDataResponse weeklyData = weeklyDataService.getWeeklyDataByWigIdAndWeek(wigId, week);
        return ResponseEntity.ok(weeklyData);
    }

    /**
     * 주간 데이터 생성
     * POST /api/weekly-data
     */
    @PostMapping("/api/weekly-data")
    public ResponseEntity<WeeklyDataResponse> createWeeklyData(
            @Valid @RequestBody WeeklyDataRequest request) {
        log.info("POST /api/weekly-data - 주간 데이터 생성 요청: wigId={}, week={}",
                request.getWigId(), request.getWeek());
        WeeklyDataResponse createdWeeklyData = weeklyDataService.createWeeklyData(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWeeklyData);
    }

    /**
     * 주간 데이터 수정
     * PUT /api/weekly-data/{id}
     */
    @PutMapping("/api/weekly-data/{id}")
    public ResponseEntity<WeeklyDataResponse> updateWeeklyData(
            @PathVariable Long id,
            @Valid @RequestBody WeeklyDataRequest request) {
        log.info("PUT /api/weekly-data/{} - 주간 데이터 수정 요청", id);
        WeeklyDataResponse updatedWeeklyData = weeklyDataService.updateWeeklyData(id, request);
        return ResponseEntity.ok(updatedWeeklyData);
    }

    /**
     * 주간 데이터 삭제
     * DELETE /api/weekly-data/{id}
     */
    @DeleteMapping("/api/weekly-data/{id}")
    public ResponseEntity<Void> deleteWeeklyData(@PathVariable Long id) {
        log.info("DELETE /api/weekly-data/{} - 주간 데이터 삭제 요청", id);
        weeklyDataService.deleteWeeklyData(id);
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

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(
            IllegalStateException e) {
        log.error("상태 오류: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
