package com.fdx.backend.domain.wig;

import com.fdx.backend.domain.MeasureType;
import com.fdx.backend.dto.WigRequest;
import com.fdx.backend.dto.WigResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * WIG 비즈니스 로직 처리 Service
 *
 * 4DX 원칙: "가장 중요한 목표(WIG)는 최대 2개까지만"
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WigService {

    private final WigRepository wigRepository;

    // 4DX 원칙: WIG는 최대 2개까지
    private static final int MAX_WIG_COUNT = 2;

    /**
     * 모든 WIG 조회 (활성 WIG 2개만)
     */
    public List<WigResponse> getAllWigs() {
        log.info("모든 WIG 조회");

        return wigRepository.findAll().stream()
                .map(WigResponse::from)
                .collect(Collectors.toList());
    }


    /**
     * WIG 개수 확인
     */
    public long countWigs() {
        return wigRepository.count();
    }

    /**
     * WIG 생성
     *
     * 4DX 원칙: WIG는 최대 2개까지만 생성 가능
     */
    @Transactional
    public WigResponse createWig(WigRequest request) {
        log.info("WIG 생성 시도: title={}", request.getTitle());

        // WIG 개수 제한 검증
        long currentCount = wigRepository.count();
        if (currentCount >= MAX_WIG_COUNT) {
            throw new IllegalStateException(
                    String.format("WIG는 최대 %d개까지만 생성할 수 있습니다. (4DX 원칙)", MAX_WIG_COUNT)
            );
        }

        // NUMERIC 타입이면 unit 검증
        if(request.getMeasureType() == MeasureType.NUMERIC &&
           (request.getUnit() == null || request.getUnit().isBlank())) {
            throw new IllegalArgumentException("수치형 목표는 단위가 필요합니다");
        }

        Wig wig = Wig.builder()
                .title(request.getTitle())
                .fromX(request.getFromX())
                .toY(request.getToY())
                .byWhen(request.getByWhen())
                .measureType(request.getMeasureType())
                .unit(request.getUnit())
                .build();

        Wig savedWig = wigRepository.save(wig);
        log.info("WIG 생성 완료: id={}, 현재 WIG 개수: {}/{}",
                savedWig.getId(), currentCount+1, MAX_WIG_COUNT);

        return WigResponse.from(savedWig);
    }


    /**
     * WIG 수정
     */
    @Transactional
    public WigResponse updateWig(Long id, WigRequest request) {
        log.info("WIG 수정: id={}", id);

        Wig wig =  wigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 WIG를 찾을 수 없습니다: " + id));

        // NUMERIC 타입이면 unit 검증
        if (request.getMeasureType() == MeasureType.NUMERIC &&
                (request.getUnit() == null || request.getUnit().isBlank())) {
            throw new IllegalArgumentException("수치형 목표는 단위가 필요합니다");
        }

        // 엔티티 수정
        wig.setTitle(request.getTitle());
        wig.setFromX(request.getFromX());
        wig.setToY(request.getToY());
        wig.setByWhen(request.getByWhen());
        wig.setMeasureType(request.getMeasureType());
        wig.setUnit(request.getUnit());

        log.info("WIG 수정 완료: id={}", id);

        return WigResponse.from(wig);
    }

    /**
     * WIG 삭제
     */
    @Transactional
    public void deleteWig(Long id) {
        log.info("WIG 삭제: id={}", id);

        if (!wigRepository.existsById(id)) {
            throw new IllegalArgumentException("해당 WIG를 찾을 수 없습니다: " + id);
        }

        wigRepository.deleteById(id);
        log.info("WIG 삭제 완료: id={}", id);
    }
}


