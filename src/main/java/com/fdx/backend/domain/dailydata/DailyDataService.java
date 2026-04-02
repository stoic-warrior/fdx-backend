package com.fdx.backend.domain.dailydata;

import com.fdx.backend.domain.GoalDirection;
import com.fdx.backend.domain.leadmeasure.LeadMeasure;
import com.fdx.backend.domain.leadmeasure.LeadMeasureRepository;
import com.fdx.backend.domain.wig.Wig;
import com.fdx.backend.domain.wig.WigRepository;
import com.fdx.backend.dto.DailyDataRequest;
import com.fdx.backend.dto.DailyDataResponse;
import com.fdx.backend.dto.StreakResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
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
    private final LeadMeasureRepository leadMeasureRepository;
    private final EntityManager entityManager;

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
     * 특정 WIG의 연속달성(Streak) 계산
     * - MAXIMIZE: actual >= dailyTarget 인 날 달성
     * - MINIMIZE: actual <= dailyTarget 인 날 달성
     * - 데이터 미입력일은 streak 중단
     */
    public StreakResponse getStreak(Long wigId) {
        Wig wig = wigRepository.findById(wigId)
                .orElseThrow(() -> new IllegalArgumentException("해당 WIG를 찾을 수 없습니다: " + wigId));
        LocalDate wigCreatedDate = wig.getCreatedAt().toLocalDate();

        List<LeadMeasure> leadMeasures = leadMeasureRepository.findByWigId(wigId).stream()
                .sorted(Comparator.comparing(LeadMeasure::getId))
                .collect(Collectors.toList());

        List<DailyData> allData = dailyDataRepository.findByWigIdOrderByDateAsc(wigId);

        if (allData.isEmpty() || leadMeasures.isEmpty()) {
            List<StreakResponse.LeadMeasureStreak> emptyStreaks = leadMeasures.stream()
                    .map(lm -> StreakResponse.LeadMeasureStreak.builder()
                            .leadMeasureId(lm.getId())
                            .name(lm.getName())
                            .currentStreak(0)
                            .direction(lm.getGoalDirection().name())
                            .build())
                    .collect(Collectors.toList());
            return StreakResponse.builder().overallStreak(0).leadMeasureStreaks(emptyStreaks).build();
        }

        // date -> DailyData 맵
        Map<LocalDate, DailyData> dataMap = allData.stream()
                .collect(Collectors.toMap(DailyData::getDate, d -> d));

        LocalDate today = LocalDate.now();
        LocalDate mostRecentEntry = allData.stream()
                .map(DailyData::getDate)
                .max(Comparator.naturalOrder())
                .orElse(today);

        // 가장 최근 입력일이 2일 이상 전이면 streak 전부 0 (어제도 미입력 = 끊김)
        long dayGap = java.time.temporal.ChronoUnit.DAYS.between(mostRecentEntry, today);
        if (dayGap > 1) {
            List<StreakResponse.LeadMeasureStreak> zeroStreaks = leadMeasures.stream()
                    .map(lm -> StreakResponse.LeadMeasureStreak.builder()
                            .leadMeasureId(lm.getId())
                            .name(lm.getName())
                            .currentStreak(0)
                            .direction(lm.getGoalDirection().name())
                            .build())
                    .collect(Collectors.toList());
            return StreakResponse.builder().overallStreak(0).leadMeasureStreaks(zeroStreaks).build();
        }

        // lead measure별 streak 계산
        List<StreakResponse.LeadMeasureStreak> streaks = new ArrayList<>();
        for (LeadMeasure lm : leadMeasures) {
            int streak = 0;

            // 오늘 달성 여부 확인
            DailyData todayData = dataMap.get(today);
            boolean todayAchieved = false;
            if (todayData != null) {
                Double todayActual = getLeadValue(todayData, lm.getId());
                todayAchieved = todayActual != null && (
                        lm.getGoalDirection() == GoalDirection.MAXIMIZE
                                ? todayActual >= lm.getDailyTarget()
                                : todayActual <= lm.getDailyTarget()
                );
            }

            LocalDate date = todayAchieved ? today : today.minusDays(1);
            while (true) {
                if (date.isBefore(wigCreatedDate)) break;
                DailyData data = dataMap.get(date);
                if (data == null) break;
                Double actual = getLeadValue(data, lm.getId());
                if (actual == null) break;
                boolean achieved = lm.getGoalDirection() == GoalDirection.MAXIMIZE
                        ? actual >= lm.getDailyTarget()
                        : actual <= lm.getDailyTarget();
                if (!achieved) break;
                streak++;
                date = date.minusDays(1);
            }
            streaks.add(StreakResponse.LeadMeasureStreak.builder()
                    .leadMeasureId(lm.getId())
                    .name(lm.getName())
                    .currentStreak(streak)
                    .direction(lm.getGoalDirection().name())
                    .build());
        }

        // 전체 동시달성 streak 계산
        boolean todayAllAchieved = false;
        DailyData todayDataForOverall = dataMap.get(today);
        if (todayDataForOverall != null) {
            todayAllAchieved = true;
            for (LeadMeasure lm : leadMeasures) {
                if (today.isBefore(wigCreatedDate)) continue;
                Double actual = getLeadValue(todayDataForOverall, lm.getId());
                if (actual == null) { todayAllAchieved = false; break; }
                boolean achieved = lm.getGoalDirection() == GoalDirection.MAXIMIZE
                        ? actual >= lm.getDailyTarget()
                        : actual <= lm.getDailyTarget();
                if (!achieved) { todayAllAchieved = false; break; }
            }
        }

        int overallStreak = 0;
        LocalDate date = todayAllAchieved ? today : today.minusDays(1);
        while (true) {
            DailyData data = dataMap.get(date);
            if (data == null) break;
            boolean allAchieved = true;
            boolean anyChecked = false;
            for (LeadMeasure lm : leadMeasures) {
                if (date.isBefore(wigCreatedDate)) continue;
                anyChecked = true;
                Double actual = getLeadValue(data, lm.getId());
                if (actual == null) { allAchieved = false; break; }
                boolean achieved = lm.getGoalDirection() == GoalDirection.MAXIMIZE
                        ? actual >= lm.getDailyTarget()
                        : actual <= lm.getDailyTarget();
                if (!achieved) { allAchieved = false; break; }
            }
            if (!allAchieved || !anyChecked) break;
            overallStreak++;
            date = date.minusDays(1);
        }

        return StreakResponse.builder()
                .overallStreak(overallStreak)
                .leadMeasureStreaks(streaks)
                .build();
    }

    /**
     * DailyData에서 특정 leadMeasureId의 값을 조회
     * 정규화된 DailyLeadData에서 FK로 직접 조회
     */
    private Double getLeadValue(DailyData data, Long leadMeasureId) {
        return data.getLeadValues().stream()
                .filter(dld -> dld.getLeadMeasure().getId().equals(leadMeasureId))
                .map(DailyLeadData::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * 일간 데이터 생성
     */
    @Transactional
    public DailyDataResponse createDailyData(DailyDataRequest request) {
        log.info("일간 데이터 생성: wigId={}, date={}", request.getWigId(), request.getDate());

        Wig wig = wigRepository.findById(request.getWigId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "해당 WIG를 찾을 수 없습니다: " + request.getWigId()));

        if (dailyDataRepository.existsByWigIdAndDate(request.getWigId(), request.getDate())) {
            throw new IllegalStateException(
                    String.format("WIG %d의 %s 데이터가 이미 존재합니다",
                            request.getWigId(), request.getDate()));
        }

        // LeadMeasure 엔티티 조회 (ID 검증)
        Map<Long, LeadMeasure> leadMeasureMap = leadMeasureRepository.findByWigId(request.getWigId())
                .stream().collect(Collectors.toMap(LeadMeasure::getId, lm -> lm));

        DailyData dailyData = DailyData.builder()
                .date(request.getDate())
                .week(request.getWeek())
                .dayOfWeek(request.getDayOfWeek())
                .wig(wig)
                .build();

        // leadValues 매핑
        if (request.getLeadValues() != null) {
            for (Map.Entry<Long, Double> entry : request.getLeadValues().entrySet()) {
                LeadMeasure lm = leadMeasureMap.get(entry.getKey());
                if (lm == null) {
                    throw new IllegalArgumentException("해당 Lead Measure를 찾을 수 없습니다: " + entry.getKey());
                }
                DailyLeadData dld = DailyLeadData.builder()
                        .dailyData(dailyData)
                        .leadMeasure(lm)
                        .value(entry.getValue())
                        .build();
                dailyData.getLeadValues().add(dld);
            }
        }

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

        // 기본 필드 수정
        dailyData.setDate(request.getDate());
        dailyData.setWeek(request.getWeek());
        dailyData.setDayOfWeek(request.getDayOfWeek());

        // LeadMeasure 엔티티 조회
        Map<Long, LeadMeasure> leadMeasureMap = leadMeasureRepository.findByWigId(dailyData.getWig().getId())
                .stream().collect(Collectors.toMap(LeadMeasure::getId, lm -> lm));

        // 기존 leadValues를 교체 (orphanRemoval=true로 기존 것은 자동 삭제)
        dailyData.getLeadValues().clear();
        entityManager.flush(); // DELETE를 먼저 실행하여 unique constraint 위반 방지
        if (request.getLeadValues() != null) {
            for (Map.Entry<Long, Double> entry : request.getLeadValues().entrySet()) {
                LeadMeasure lm = leadMeasureMap.get(entry.getKey());
                if (lm == null) {
                    throw new IllegalArgumentException("해당 Lead Measure를 찾을 수 없습니다: " + entry.getKey());
                }
                DailyLeadData dld = DailyLeadData.builder()
                        .dailyData(dailyData)
                        .leadMeasure(lm)
                        .value(entry.getValue())
                        .build();
                dailyData.getLeadValues().add(dld);
            }
        }

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
