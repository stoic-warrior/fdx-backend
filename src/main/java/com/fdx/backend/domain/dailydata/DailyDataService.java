package com.fdx.backend.domain.dailydata;

import com.fdx.backend.domain.wig.Wig;
import com.fdx.backend.domain.wig.WigRepository;
import com.fdx.backend.dto.DailyDataRequest;
import com.fdx.backend.dto.DailyDataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DailyData 비즈니스 로직 처리 Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DailyDataService {

    private final DailyDataRepository dailyDataRepository;
    private final WigRepository wigRepository;

    /**
     * 특정 WIG의 모든 일간 데이터 조회
     */
    public List<DailyDataResponse> getDailyDataByWigId(Long wigId) {
        log.info("WIG {}의 일간 데이터 조회", wigId);

        if (!wigRepository.existsById(wigId)) {
            throw new IllegalArgumentException("해당 WIG를 찾을 수 없습니다: " + wigId);
        }

        return dailyDataRepository.findByWigIdOrderByDateAsc(wigId).stream()
                .map(DailyDataResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 WIG의 특정 주차 일간 데이터 조회
     */
    public List<DailyDataResponse> getDailyDataByWigIdAndWeek(Long wigId, String week) {
        log.info("WIG {}의 {} 일간 데이터 조회", wigId, week);

        if (!wigRepository.existsById(wigId)) {
            throw new IllegalArgumentException("해당 WIG를 찾을 수 없습니다: " + wigId);
        }

        return dailyDataRepository.findByWigIdAndWeekOrderByDateAsc(wigId, week).stream()
                .map(DailyDataResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 WIG의 날짜 범위 일간 데이터 조회
     */
    public List<DailyDataResponse> getDailyDataByDateRange(
            Long wigId, LocalDate startDate, LocalDate endDate) {
        log.info("WIG {}의 일간 데이터 조회: {} ~ {}", wigId, startDate, endDate);

        if (!wigRepository.existsById(wigId)) {
            throw new IllegalArgumentException("해당 WIG를 찾을 수 없습니다: " + wigId);
        }

        return dailyDataRepository
                .findByWigIdAndDateBetweenOrderByDateAsc(wigId, startDate, endDate)
                .stream()
                .map(DailyDataResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 일간 데이터 생성
     */
    @Transactional
    public DailyDataResponse createDailyData(DailyDataRequest request) {
        log.info("일간 데이터 생성: wigId={}, date={}", request.getWigId(), request.getDate());

        // WIG 조회
        Wig wig = wigRepository.findById(request.getWigId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 WIG를 찾을 수 없습니다: " + request.getWigId()));

        // 중복 체크
        if (dailyDataRepository.existsByWigIdAndDate(request.getWigId(), request.getDate())) {
            throw new IllegalStateException(
                    String.format("WIG %d의 %s 데이터가 이미 존재합니다",
                            request.getWigId(), request.getDate()));
        }

        // 일간 데이터 생성
        DailyData dailyData = DailyData.builder()
                .date(request.getDate())
                .week(request.getWeek())
                .dayOfWeek(request.getDayOfWeek())
                .lead1(request.getLead1())
                .lead2(request.getLead2())
                .wig(wig)
                .build();

        DailyData savedDailyData = dailyDataRepository.save(dailyData);
        log.info("일간 데이터 생성 완료: id={}", savedDailyData.getId());

        return DailyDataResponse.from(savedDailyData);
    }

    /**
     * 일간 데이터 수정
     */
    @Transactional
    public DailyDataResponse updateDailyData(Long id, DailyDataRequest request) {
        log.info("일간 데이터 수정: id={}", id);

        DailyData dailyData = dailyDataRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 일간 데이터를 찾을 수 없습니다: " + id));

        // 엔티티 수정
        dailyData.setDate(request.getDate());
        dailyData.setWeek(request.getWeek());
        dailyData.setDayOfWeek(request.getDayOfWeek());
        dailyData.setLead1(request.getLead1());
        dailyData.setLead2(request.getLead2());

        log.info("일간 데이터 수정 완료: id={}", id);

        return DailyDataResponse.from(dailyData);
    }

    /**
     * 일간 데이터 삭제
     */
    @Transactional
    public void deleteDailyData(Long id) {
        log.info("일간 데이터 삭제: id={}", id);

        if (!dailyDataRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 일간 데이터를 찾을 수 없습니다: " + id);
        }

        dailyDataRepository.deleteById(id);
        log.info("일간 데이터 삭제 완료: id={}", id);
    }

}
