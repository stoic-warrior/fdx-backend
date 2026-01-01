package com.fdx.backend.domain.wig;

import com.fdx.backend.domain.MeasureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * WIG Repository
 * Spring Data JPA가 자동으로 구현체를 생성합니다
 */
@Repository
public interface WigRepository extends JpaRepository<Wig, Long> {

    // 측정 유형별 조회
    List<Wig> findByMeasureType(MeasureType measureType);

    // 제목으로 검색 (대소문자 무시, 부분 일치)
    List<Wig> findByTitleContainingIgnoreCase(String keyword);
}