package com.fdx.backend.domain.dailydata;

import com.fdx.backend.dto.DailyDataRequest;
import com.fdx.backend.dto.DailyDataResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DailyData REST API Controller
 *
 * API 엔드포인트:
 * GET    /api/wigs/{wigId}/daily-data                    - 특정 WIG의 모든 일간 데이터 조회
 * GET    /api/wigs/{wigId}/daily-data/week/{week}        - 특정 WIG의 특정 주차 일간 데이터 조회
 * GET    /api/wigs/{wigId}/daily-data/range              - 특정 WIG의 날짜 범위 일간 데이터 조회
 * POST   /api/daily-data                                  - 일간 데이터 생성
 * PUT    /api/daily-data/{id}                             - 일간 데이터 수정
 * DELETE /api/daily-data/{id}                             - 일간 데이터 삭제
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class DailyDataContoroller {

    private final DailyDataService dailyDataService;

    /**
     * 특정 WIG의 모든 일간 데이터 조회
     * GET /api/wigs/{wigId}/daily-data
     */
    @GetMapping("/api/wigs/{wigId}/daily-data")
    public ResponseEntity<List<DailyDataResponse>> getDailyDataByWigId(
            @PathVariable Long wigId) {
        log.info("GET /api/wigs/{}/daily-data - 일간 데이터 조회 요청", wigId);
        List<DailyDataResponse> dailyData = dailyDataService.getDailyDataByWigId(wigId);
        return ResponseEntity.ok(dailyData);
    }

    /**
     * 특정 WIG의 특정 주차 일간 데이터 조회
     * GET /api/wigs/{wigId}/daily-data/week/{week}
     */
    @GetMapping("/api/wigs/{wigId}/daily-data/week/{week}")
    public ResponseEntity<List<DailyDataResponse>> getDailyDataByWigIdAndWeek(
            @PathVariable Long wigId,
            @PathVariable String week) {
        log.info("GET /api/wigs/{}/daily-data/week/{} - 주차별 일간 데이터 조회 요청", wigId, week);
        List<DailyDataResponse> dailyData = dailyDataService.getDailyDataByWigIdAndWeek(wigId, week);
        return ResponseEntity.ok(dailyData);
    }

    /**
     * 특정 WIG의 날짜 범위 일간 데이터 조회
     * GET /api/wigs/{wigId}/daily-data/range?startDate=2025-01-01&endDate=2025-01-07
     */
    @GetMapping("/api/wigs/{wigId}/daily-data/range")
    public ResponseEntity<List<DailyDataResponse>> getDailyDataByDateRange(
            @PathVariable Long wigId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("GET /api/wigs/{}/daily-data/range - 날짜 범위 일간 데이터 조회 요청: {} ~ {}",
                wigId, startDate, endDate);
        List<DailyDataResponse> dailyData =
                dailyDataService.getDailyDataByDateRange(wigId, startDate, endDate);
        return ResponseEntity.ok(dailyData);
    }

    /**
     * 일간 데이터 생성
     * POST /api/daily-data
     */
    @PostMapping("/api/daily-data")
    public ResponseEntity<DailyDataResponse> createDailyData(
            @Valid @RequestBody DailyDataRequest request) {
        log.info("POST /api/daily-data - 일간 데이터 생성 요청: wigId={}, date={}",
                request.getWigId(), request.getDate());
        DailyDataResponse createdDailyData = dailyDataService.createDailyData(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDailyData);
    }

    /**
     * 일간 데이터 수정
     * PUT /api/daily-data/{id}
     */
    @PutMapping("/api/daily-data/{id}")
    public ResponseEntity<DailyDataResponse> updateDailyData(
            @PathVariable Long id,
            @Valid @RequestBody DailyDataRequest request) {
        log.info("PUT /api/daily-data/{} - 일간 데이터 수정 요청", id);
        DailyDataResponse updatedDailyData = dailyDataService.updateDailyData(id, request);
        return ResponseEntity.ok(updatedDailyData);
    }

    /**
     * 일간 데이터 삭제
     * DELETE /api/daily-data/{id}
     */
    @DeleteMapping("/api/daily-data/{id}")
    public ResponseEntity<Void> deleteDailyData(@PathVariable Long id) {
        log.info("DELETE /api/daily-data/{} - 일간 데이터 삭제 요청", id);
        dailyDataService.deleteDailyData(id);
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
