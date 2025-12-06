-- 초기 테스트 데이터
-- 애플리케이션 시작 시 자동으로 실행됩니다

-- WIG 1: 백엔드 개발자 취업 (상태형)
INSERT INTO wigs (title, from_x, to_y, by_when, measure_type, unit, created_at, updated_at)
VALUES ('백엔드 개발자 취업', '백수', '취업 성공', '2025-12-31', 'STATE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- WIG 2: 체중 감량 (수치형)
INSERT INTO wigs (title, from_x, to_y, by_when, measure_type, unit, created_at, updated_at)
VALUES ('체중 감량', '75', '68', '2025-06-30', 'NUMERIC', 'kg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- WIG 3: 영어 회화 실력 향상 (상태형)
INSERT INTO wigs (title, from_x, to_y, by_when, measure_type, unit, created_at, updated_at)
VALUES ('영어 회화 실력 향상', '기초', '비즈니스 레벨', '2025-09-30', 'STATE', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- WIG 4: 월 수입 증대 (수치형)
INSERT INTO wigs (title, from_x, to_y, by_when, measure_type, unit, created_at, updated_at)
VALUES ('월 수입 증대', '0', '300', '2025-08-31', 'NUMERIC', '만원', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);