package com.fdx.backend.domain.dailydata;

import com.fdx.backend.domain.GoalDirection;
import com.fdx.backend.domain.leadmeasure.LeadMeasure;
import com.fdx.backend.domain.leadmeasure.LeadMeasureRepository;
import com.fdx.backend.domain.wig.Wig;
import com.fdx.backend.domain.wig.WigRepository;
import com.fdx.backend.dto.DailyDataRequest;
import com.fdx.backend.dto.DailyDataResponse;
import com.fdx.backend.dto.StreakResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
        if (!wigRepository.existsById(wigId)) {
            throw new IllegalArgumentException("해당 WIG를 찾을 수 없습니다: " + wigId);
        }

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
        // 오늘 달성 여부와 무관하게 오늘은 "진행 중"으로 간주 → 오늘 달성했으면 today부터, 미달성이면 yesterday부터 시작
        List<StreakResponse.LeadMeasureStreak> streaks = new ArrayList<>();
        for (int i = 0; i < leadMeasures.size(); i++) {
            LeadMeasure lm = leadMeasures.get(i);
            int streak = 0;

            // 오늘 달성 여부 확인
            DailyData todayData = dataMap.get(today);
            boolean todayAchieved = false;
            if (todayData != null) {
                Double todayActual = getLeadValue(todayData, i);
                todayAchieved = todayActual != null && (
                        lm.getGoalDirection() == GoalDirection.MAXIMIZE
                                ? todayActual >= lm.getDailyTarget()
                                : todayActual <= lm.getDailyTarget()
                );
            }

            LocalDate lmCreatedDate = lm.getCreatedAt().toLocalDate();
            LocalDate date = todayAchieved ? today : today.minusDays(1);
            while (true) {
                // 리드매셔 생성일 이전이면 "존재하지 않았던 것"이므로 끊지 않고 종료
                if (date.isBefore(lmCreatedDate)) break;
                DailyData data = dataMap.get(date);
                if (data == null) break;
                Double actual = getLeadValue(data, i);
                // 생성 후 데이터가 null이면 미입력 = 실패
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
        // 오늘 모두 달성했으면 today부터, 아니면 yesterday부터
        boolean todayAllAchieved = false;
        DailyData todayDataForOverall = dataMap.get(today);
        if (todayDataForOverall != null) {
            todayAllAchieved = true;
            for (int i = 0; i < leadMeasures.size(); i++) {
                LeadMeasure lm = leadMeasures.get(i);
                // 오늘 생성된 리드매셔가 아직 입력 안됐으면 진행 중으로 간주
                if (today.isBefore(lm.getCreatedAt().toLocalDate())) continue;
                Double actual = getLeadValue(todayDataForOverall, i);
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
            for (int i = 0; i < leadMeasures.size(); i++) {
                LeadMeasure lm = leadMeasures.get(i);
                // 해당 날짜에 존재하지 않았던 리드매셔는 skip (실패로 보지 않음)
                if (date.isBefore(lm.getCreatedAt().toLocalDate())) continue;
                Double actual = getLeadValue(data, i);
                if (actual == null) { allAchieved = false; break; }
                boolean achieved = lm.getGoalDirection() == GoalDirection.MAXIMIZE
                        ? actual >= lm.getDailyTarget()
                        : actual <= lm.getDailyTarget();
                if (!achieved) { allAchieved = false; break; }
            }
            if (!allAchieved) break;
            overallStreak++;
            date = date.minusDays(1);
        }

        return StreakResponse.builder()
                .overallStreak(overallStreak)
                .leadMeasureStreaks(streaks)
                .build();
    }

    private Double getLeadValue(DailyData data, int idx) {
        return switch (idx) {
            case 0 -> data.getLead1();
            case 1 -> data.getLead2();
            case 2 -> data.getLead3();
            case 3 -> data.getLead4();
            case 4 -> data.getLead5();
            default -> null;
        };
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
                .lead3(request.getLead3())
                .lead4(request.getLead4())
                .lead5(request.getLead5())
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
        dailyData.setLead3(request.getLead3());
        dailyData.setLead4(request.getLead4());
        dailyData.setLead5(request.getLead5());

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
