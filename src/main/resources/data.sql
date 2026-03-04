-- 초기 테스트 데이터
-- 애플리케이션 시작 시 자동으로 실행됩니다

-- ======================================
-- Users (사용자)
-- ======================================
-- 비밀번호: password123 (BCrypt 암호화됨)
INSERT INTO users (email, password, name, role, provider, created_at, updated_at)
VALUES ('test@example.com', '$2a$10$QNJceIh.R/ck1ddWH4CdUOOmcHocLjb/P1KEX5Uh67yXRyOoZCHxO', '테스트유저', 'USER', 'LOCAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (email, password, name, role, provider, created_at, updated_at)
VALUES ('admin@example.com', '$2a$10$QNJceIh.R/ck1ddWH4CdUOOmcHocLjb/P1KEX5Uh67yXRyOoZCHxO', '관리자', 'ADMIN', 'LOCAL', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ======================================
-- WIGs (목표)
-- created_at을 2025-01-01로 설정하여 테스트 데이터가 유효하도록 함
-- ======================================

-- WIG 1: 백엔드 개발자 취업 (상태형) - user_id=1
INSERT INTO wigs (title, fromx, toy, by_when, measure_type, unit, user_id, created_at, updated_at)
VALUES ('백엔드 개발자 취업', '백수', '취업 성공', '2025-12-31', 'STATE', NULL, 1, '2025-01-01 00:00:00', CURRENT_TIMESTAMP);

-- WIG 2: 체중 감량 (수치형) - user_id=1
INSERT INTO wigs (title, fromx, toy, by_when, measure_type, unit, user_id, created_at, updated_at)
VALUES ('체중 감량', '75', '68', '2025-06-30', 'NUMERIC', 'kg', 1, '2025-01-01 00:00:00', CURRENT_TIMESTAMP);

-- ======================================
-- Lead Measures (선행지표)
-- ======================================

-- WIG 1 (백엔드 개발자 취업)의 Lead Measures
INSERT INTO lead_measures (name, daily_target, weekly_target, unit, goal_direction, wig_id, created_at, updated_at)
VALUES ('코딩 시간', 6, 42, '시간', 'MAXIMIZE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO lead_measures (name, daily_target, weekly_target, unit, goal_direction, wig_id, created_at, updated_at)
VALUES ('이력서 제출', 0.5, 3, '개', 'MAXIMIZE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- WIG 2 (체중 감량)의 Lead Measures
INSERT INTO lead_measures (name, daily_target, weekly_target, unit, goal_direction, wig_id, created_at, updated_at)
VALUES ('운동 시간', 60, 420, '분', 'MAXIMIZE', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO lead_measures (name, daily_target, weekly_target, unit, goal_direction, wig_id, created_at, updated_at)
VALUES ('칼로리 섭취', 1800, 12600, 'kcal', 'MINIMIZE', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ======================================
-- Milestones (마일스톤 - STATE 타입 WIG 전용)
-- ======================================

-- WIG 1 (백엔드 개발자 취업)의 Milestones
INSERT INTO milestones (name, completed, order_index, wig_id, created_at, updated_at)
VALUES ('포트폴리오 3개 완성', TRUE, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO milestones (name, completed, order_index, wig_id, created_at, updated_at)
VALUES ('Spring Boot 프로젝트 완성', TRUE, 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO milestones (name, completed, order_index, wig_id, created_at, updated_at)
VALUES ('이력서 10곳 제출', FALSE, 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO milestones (name, completed, order_index, wig_id, created_at, updated_at)
VALUES ('면접 5회 진행', FALSE, 4, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO milestones (name, completed, order_index, wig_id, created_at, updated_at)
VALUES ('최종 합격', FALSE, 5, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ======================================
-- Commitments (주간 약속)
-- ======================================

-- WIG 1 (백엔드 개발자 취업)의 W1 Commitments
INSERT INTO commitments (text, week, completed, wig_id, created_at, updated_at)
VALUES ('백엔드 로드맵 정리', 'W1', TRUE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO commitments (text, week, completed, wig_id, created_at, updated_at)
VALUES ('Spring Security 공부', 'W1', TRUE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- WIG 1 (백엔드 개발자 취업)의 W2 Commitments
INSERT INTO commitments (text, week, completed, wig_id, created_at, updated_at)
VALUES ('코딩테스트 3문제 풀기', 'W2', FALSE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO commitments (text, week, completed, wig_id, created_at, updated_at)
VALUES ('JPA 심화 학습', 'W2', FALSE, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- WIG 2 (체중 감량)의 W1 Commitments
INSERT INTO commitments (text, week, completed, wig_id, created_at, updated_at)
VALUES ('매일 아침 30분 조깅', 'W1', TRUE, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO commitments (text, week, completed, wig_id, created_at, updated_at)
VALUES ('야식 끊기', 'W1', FALSE, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ======================================
-- Weekly Data (주간 실적)
-- ======================================

-- WIG 1 (백엔드 개발자 취업 - STATE 타입)의 주간 데이터
INSERT INTO weekly_data (week, milestone_progress, lead1, lead2, wig_id, created_at, updated_at)
VALUES ('W1', 20, 35, 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO weekly_data (week, milestone_progress, lead1, lead2, wig_id, created_at, updated_at)
VALUES ('W2', 40, 46, 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- WIG 2 (체중 감량 - NUMERIC 타입)의 주간 데이터
INSERT INTO weekly_data (week, actual, target, lead1, lead2, wig_id, created_at, updated_at)
VALUES ('W1', 74.5, 74, 320, 13500, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO weekly_data (week, actual, target, lead1, lead2, wig_id, created_at, updated_at)
VALUES ('W2', 73.8, 73, 385, 12950, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ======================================
-- Daily Data (일간 실적)
-- WIG created_at이 2025-01-01이므로 그 이후 날짜로 설정
-- ======================================

-- WIG 1의 W1 일간 데이터 (2025-01-06 ~ 2025-01-10, 월~금)
INSERT INTO daily_data (date, week, day_of_week, lead1, lead2, wig_id, created_at, updated_at)
VALUES ('2025-01-06', 'W1', '월', 5, 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, lead1, lead2, wig_id, created_at, updated_at)
VALUES ('2025-01-07', 'W1', '화', 7, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, lead1, lead2, wig_id, created_at, updated_at)
VALUES ('2025-01-08', 'W1', '수', 8, 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, lead1, lead2, wig_id, created_at, updated_at)
VALUES ('2025-01-09', 'W1', '목', 6, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, lead1, lead2, wig_id, created_at, updated_at)
VALUES ('2025-01-10', 'W1', '금', 9, 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- WIG 2의 W1 일간 데이터 (2025-01-06 ~ 2025-01-10, 월~금)
INSERT INTO daily_data (date, week, day_of_week, lead1, lead2, wig_id, created_at, updated_at)
VALUES ('2025-01-06', 'W1', '월', 60, 1750, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, lead1, lead2, wig_id, created_at, updated_at)
VALUES ('2025-01-07', 'W1', '화', 45, 1900, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, lead1, lead2, wig_id, created_at, updated_at)
VALUES ('2025-01-08', 'W1', '수', 70, 1700, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, lead1, lead2, wig_id, created_at, updated_at)
VALUES ('2025-01-09', 'W1', '목', 50, 1850, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, lead1, lead2, wig_id, created_at, updated_at)
VALUES ('2025-01-10', 'W1', '금', 95, 2300, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- WIG 2의 W2 일간 데이터 (2025-01-13 ~ 2025-01-17, 월~금)
INSERT INTO daily_data (date, week, day_of_week, lead1, lead2, wig_id, created_at, updated_at)
VALUES ('2025-01-13', 'W2', '월', 55, 1800, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, lead1, lead2, wig_id, created_at, updated_at)
VALUES ('2025-01-14', 'W2', '화', 80, 1650, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, lead1, lead2, wig_id, created_at, updated_at)
VALUES ('2025-01-15', 'W2', '수', 65, 1750, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, lead1, lead2, wig_id, created_at, updated_at)
VALUES ('2025-01-16', 'W2', '목', 90, 1700, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, lead1, lead2, wig_id, created_at, updated_at)
VALUES ('2025-01-17', 'W2', '금', 95, 2050, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);