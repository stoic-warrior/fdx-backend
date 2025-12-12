package com.fdx.backend.domain.leadmeasure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Lead Measure Repository
 */
@Repository
public interface LeadMeasureRepository extends JpaRepository<LeadMeasure, Long> { // 구현체는 Spring Data JPA가 런타임에 자동 생성
    // LeadMeasure : 관리 대상 엔티티
    // Long : PK 타입

    /**
     * 특정 WIG의 모든 Lead Measures 조회
     */
    List<LeadMeasure> findByWigId(Long wigId) // jpa는 메서드 이름을 엔티티 필드로 파싱
}
