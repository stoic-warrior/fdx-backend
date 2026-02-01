package com.fdx.backend.domain.wig;

import com.fdx.backend.domain.MeasureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * WIG Repository
 */
@Repository
public interface WigRepository extends JpaRepository<Wig, Long> {

    /**
     * 특정 사용자의 모든 WIG 조회
     */
    List<Wig> findByUserId(Long userId);

    /**
     * 특정 사용자의 WIG 개수 조회
     */
    long countByUserId(Long userId);

    /**
     * 특정 사용자의 측정 유형별 WIG 조회
     */
    List<Wig> findByUserIdAndMeasureType(Long userId, MeasureType measureType);

    /**
     * 제목으로 검색 (대소문자 무시, 부분 일치)
     */
    List<Wig> findByUserIdAndTitleContainingIgnoreCase(Long userId, String keyword);

    // 기존 메서드 (하위 호환용)
    List<Wig> findByMeasureType(MeasureType measureType);
    List<Wig> findByTitleContainingIgnoreCase(String keyword);
}