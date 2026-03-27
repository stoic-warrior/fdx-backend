package com.fdx.backend.domain;

/**
 * Lead Measure의 측정 유형
 * NUMERIC: 수치형 (예: 코딩 6시간, 운동 60분)
 * BOOLEAN: OX형 (예: 오늘 운동 했는가? O/X)
 */
public enum LeadMeasureType {
    NUMERIC,
    BOOLEAN
}
