package com.fdx.backend.domain;

/**
 * 목표(WIG)의 측정 유형
 * NUMERIC: 수치형 (예: 체중 75kg -> 68kg)
 * STATE: 상태형 (예: 백수 -> 취업 성공, 마일스톤 기반)
 */
public enum MeasureType {
    NUMERIC,
    STATE
}
