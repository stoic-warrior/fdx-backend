-- 초기 테스트 데이터
-- 애플리케이션 시작 시 자동으로 실행됩니다

-- WIG 1: 백엔드 개발자 취업 (상태형)
INSERT INTO wigs (title, fromx, toy, by_when, measure_type, unit, created_at, updated_at)
VALUES ('백엔드 개발자 취업', '백수', '취업 성공', '2025-12-31', 'STATE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- WIG 2: 체중 감량 (수치형)
INSERT INTO wigs (title, fromx, toy, by_when, measure_type, unit, created_at, updated_at)
VALUES ('체중 감량', '75', '68', '2025-06-30', 'NUMERIC', 'kg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ======================================
-- Lead Measures (선행지표)
-- ======================================

-- WIG 1 (백엔드 개발자 취업)의 Lead Measures
INSERT INTO lead_measures (name, daily_target, weekly_target, unit, wig_id, created_at, updated_at)
VALUES ('코딩 시간', 6, 42, '시간', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO lead_measures (name, daily_target, weekly_target, unit, wig_id, created_at, updated_at)
VALUES ('이력서 제출', 0.5, 3, '개', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- WIG 2 (체중 감량)의 Lead Measures
INSERT INTO lead_measures (name, daily_target, weekly_target, unit, wig_id, created_at, updated_at)
VALUES ('운동 시간', 60, 420, '분', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO lead_measures (name, daily_target, weekly_target, unit, wig_id, created_at, updated_at)
VALUES ('칼로리 섭취', 1800, 12600, 'kcal', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);