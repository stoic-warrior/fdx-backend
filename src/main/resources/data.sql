-- 초기 테스트 데이터
-- 애플리케이션 시작 시 자동으로 실행됩니다
-- 정규화: daily_lead_data, weekly_lead_data 테이블 사용

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
-- WIG1: id=1(코딩 시간), id=2(이력서 제출)
-- WIG2: id=3(운동 시간), id=4(칼로리 섭취)
-- ======================================

-- WIG 1 (백엔드 개발자 취업)의 Lead Measures
INSERT INTO lead_measures (name, daily_target, weekly_target, unit, goal_direction, lead_measure_type, wig_id, created_at, updated_at)
VALUES ('코딩 시간', 6, 42, '시간', 'MAXIMIZE', 'NUMERIC', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO lead_measures (name, daily_target, weekly_target, unit, goal_direction, lead_measure_type, wig_id, created_at, updated_at)
VALUES ('이력서 제출', 0.5, 3, '개', 'MAXIMIZE', 'NUMERIC', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- WIG 2 (체중 감량)의 Lead Measures
INSERT INTO lead_measures (name, daily_target, weekly_target, unit, goal_direction, lead_measure_type, wig_id, created_at, updated_at)
VALUES ('운동 시간', 60, 420, '분', 'MAXIMIZE', 'NUMERIC', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO lead_measures (name, daily_target, weekly_target, unit, goal_direction, lead_measure_type, wig_id, created_at, updated_at)
VALUES ('칼로리 섭취', 1800, 12600, 'kcal', 'MINIMIZE', 'NUMERIC', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

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
-- Weekly Data (주간 실적) - 정규화
-- ======================================

-- WIG 1 (백엔드 개발자 취업 - STATE 타입)의 주간 데이터
INSERT INTO weekly_data (week, milestone_progress, wig_id, created_at, updated_at)
VALUES ('W1', 20, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO weekly_data (week, milestone_progress, wig_id, created_at, updated_at)
VALUES ('W2', 40, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- WIG 2 (체중 감량 - NUMERIC 타입)의 주간 데이터
INSERT INTO weekly_data (week, actual, target, wig_id, created_at, updated_at)
VALUES ('W1', 74.5, 74, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO weekly_data (week, actual, target, wig_id, created_at, updated_at)
VALUES ('W2', 73.8, 73, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ======================================
-- Weekly Lead Data (주간 리드매셔 실적 - 정규화 테이블)
-- weekly_data id: 1(WIG1-W1), 2(WIG1-W2), 3(WIG2-W1), 4(WIG2-W2)
-- lead_measure id: 1(코딩시간), 2(이력서), 3(운동), 4(칼로리)
-- ======================================

-- WIG1-W1: 코딩 35시간, 이력서 2개
INSERT INTO weekly_lead_data (weekly_data_id, lead_measure_id, lead_value) VALUES (1, 1, 35);
INSERT INTO weekly_lead_data (weekly_data_id, lead_measure_id, lead_value) VALUES (1, 2, 2);

-- WIG1-W2: 코딩 46시간, 이력서 3개
INSERT INTO weekly_lead_data (weekly_data_id, lead_measure_id, lead_value) VALUES (2, 1, 46);
INSERT INTO weekly_lead_data (weekly_data_id, lead_measure_id, lead_value) VALUES (2, 2, 3);

-- WIG2-W1: 운동 320분, 칼로리 13500kcal
INSERT INTO weekly_lead_data (weekly_data_id, lead_measure_id, lead_value) VALUES (3, 3, 320);
INSERT INTO weekly_lead_data (weekly_data_id, lead_measure_id, lead_value) VALUES (3, 4, 13500);

-- WIG2-W2: 운동 385분, 칼로리 12950kcal
INSERT INTO weekly_lead_data (weekly_data_id, lead_measure_id, lead_value) VALUES (4, 3, 385);
INSERT INTO weekly_lead_data (weekly_data_id, lead_measure_id, lead_value) VALUES (4, 4, 12950);

-- ======================================
-- Daily Data (일간 실적) - 정규화
-- ======================================

-- WIG 1의 W1 일간 데이터 (2025-01-06 ~ 2025-01-10, 월~금)
INSERT INTO daily_data (date, week, day_of_week, wig_id, created_at, updated_at)
VALUES ('2025-01-06', 'W1', '월', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, wig_id, created_at, updated_at)
VALUES ('2025-01-07', 'W1', '화', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, wig_id, created_at, updated_at)
VALUES ('2025-01-08', 'W1', '수', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, wig_id, created_at, updated_at)
VALUES ('2025-01-09', 'W1', '목', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, wig_id, created_at, updated_at)
VALUES ('2025-01-10', 'W1', '금', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- WIG 2의 W1 일간 데이터 (2025-01-06 ~ 2025-01-10, 월~금)
INSERT INTO daily_data (date, week, day_of_week, wig_id, created_at, updated_at)
VALUES ('2025-01-06', 'W1', '월', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, wig_id, created_at, updated_at)
VALUES ('2025-01-07', 'W1', '화', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, wig_id, created_at, updated_at)
VALUES ('2025-01-08', 'W1', '수', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, wig_id, created_at, updated_at)
VALUES ('2025-01-09', 'W1', '목', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, wig_id, created_at, updated_at)
VALUES ('2025-01-10', 'W1', '금', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- WIG 2의 W2 일간 데이터 (2025-01-13 ~ 2025-01-17, 월~금)
INSERT INTO daily_data (date, week, day_of_week, wig_id, created_at, updated_at)
VALUES ('2025-01-13', 'W2', '월', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, wig_id, created_at, updated_at)
VALUES ('2025-01-14', 'W2', '화', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, wig_id, created_at, updated_at)
VALUES ('2025-01-15', 'W2', '수', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, wig_id, created_at, updated_at)
VALUES ('2025-01-16', 'W2', '목', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO daily_data (date, week, day_of_week, wig_id, created_at, updated_at)
VALUES ('2025-01-17', 'W2', '금', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ======================================
-- Daily Lead Data (일간 리드매셔 실적 - 정규화 테이블)
-- daily_data id: 1~5(WIG1-W1), 6~10(WIG2-W1), 11~15(WIG2-W2)
-- lead_measure id: 1(코딩시간), 2(이력서), 3(운동), 4(칼로리)
-- ======================================

-- WIG1 W1: 코딩시간(lm=1), 이력서(lm=2)
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (1, 1, 5);    -- 01-06 코딩 5시간
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (1, 2, 0);    -- 01-06 이력서 0개
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (2, 1, 7);    -- 01-07 코딩 7시간
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (2, 2, 1);    -- 01-07 이력서 1개
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (3, 1, 8);    -- 01-08 코딩 8시간
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (3, 2, 0);    -- 01-08 이력서 0개
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (4, 1, 6);    -- 01-09 코딩 6시간
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (4, 2, 1);    -- 01-09 이력서 1개
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (5, 1, 9);    -- 01-10 코딩 9시간
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (5, 2, 0);    -- 01-10 이력서 0개

-- WIG2 W1: 운동시간(lm=3), 칼로리(lm=4)
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (6, 3, 60);    -- 01-06 운동 60분
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (6, 4, 1750);  -- 01-06 칼로리 1750
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (7, 3, 45);    -- 01-07 운동 45분
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (7, 4, 1900);  -- 01-07 칼로리 1900
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (8, 3, 70);    -- 01-08 운동 70분
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (8, 4, 1700);  -- 01-08 칼로리 1700
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (9, 3, 50);    -- 01-09 운동 50분
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (9, 4, 1850);  -- 01-09 칼로리 1850
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (10, 3, 95);   -- 01-10 운동 95분
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (10, 4, 2300); -- 01-10 칼로리 2300

-- WIG2 W2: 운동시간(lm=3), 칼로리(lm=4)
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (11, 3, 55);   -- 01-13 운동 55분
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (11, 4, 1800); -- 01-13 칼로리 1800
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (12, 3, 80);   -- 01-14 운동 80분
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (12, 4, 1650); -- 01-14 칼로리 1650
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (13, 3, 65);   -- 01-15 운동 65분
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (13, 4, 1750); -- 01-15 칼로리 1750
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (14, 3, 90);   -- 01-16 운동 90분
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (14, 4, 1700); -- 01-16 칼로리 1700
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (15, 3, 95);   -- 01-17 운동 95분
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value) VALUES (15, 4, 2050); -- 01-17 칼로리 2050
