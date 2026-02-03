package com.fdx.backend.domain;

/**
 * Lead Measure의 목표 방향
 * - MAXIMIZE: 높을수록 좋음 (운동 시간, 코딩 시간, 매출)
 * - MINIMIZE: 낮을수록 좋음 (칼로리 섭취, 지출, 체중)
 */
public enum GoalDirection {
    MAXIMIZE,  // 높을수록 좋음
    MINIMIZE   // 낮을수록 좋음
}
