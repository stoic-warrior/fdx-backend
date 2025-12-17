package com.fdx.backend.domain.milestone;

import com.fdx.backend.domain.wig.WigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Milestone 비즈니스 로직 처리 Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final WigRepository wigRepository;

    /**
     * 특정 WIG의 모든 Milestones 조회
     */
}
