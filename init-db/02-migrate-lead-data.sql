-- ============================================================
-- 리드매셔 데이터 마이그레이션 스크립트
-- daily_data.lead1~5 / weekly_data.lead1~5 → 정규화 테이블로 이동
--
-- 기존 매핑 방식: lead1 = WIG의 첫 번째 리드매셔(ID순), lead2 = 두 번째, ...
-- 새 매핑 방식: (daily_data_id, lead_measure_id, lead_value) FK 직접 연결
--
-- ⚠️ 운영 MySQL DB에서 실행하세요
-- ⚠️ 실행 전 반드시 백업: mysqldump -u fdxuser -p fdxdb > backup.sql
-- ============================================================

-- 중복 방지: 이미 마이그레이션된 데이터가 있으면 건너뜀
-- (daily_lead_data, weekly_lead_data에 데이터가 없을 때만 실행)

-- ============================================================
-- 1. daily_data → daily_lead_data 마이그레이션
-- ============================================================

-- lead_measures를 WIG별로 ID순 번호 매기기 (lead1=1번째, lead2=2번째, ...)
-- lead1 마이그레이션
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value)
SELECT dd.id, lm_ranked.id, dd.lead1
FROM daily_data dd
JOIN (
    SELECT id, wig_id, ROW_NUMBER() OVER (PARTITION BY wig_id ORDER BY id) AS rn
    FROM lead_measures
) lm_ranked ON lm_ranked.wig_id = dd.wig_id AND lm_ranked.rn = 1
WHERE dd.lead1 IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM daily_lead_data dld
    WHERE dld.daily_data_id = dd.id AND dld.lead_measure_id = lm_ranked.id
  );

-- lead2 마이그레이션
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value)
SELECT dd.id, lm_ranked.id, dd.lead2
FROM daily_data dd
JOIN (
    SELECT id, wig_id, ROW_NUMBER() OVER (PARTITION BY wig_id ORDER BY id) AS rn
    FROM lead_measures
) lm_ranked ON lm_ranked.wig_id = dd.wig_id AND lm_ranked.rn = 2
WHERE dd.lead2 IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM daily_lead_data dld
    WHERE dld.daily_data_id = dd.id AND dld.lead_measure_id = lm_ranked.id
  );

-- lead3 마이그레이션
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value)
SELECT dd.id, lm_ranked.id, dd.lead3
FROM daily_data dd
JOIN (
    SELECT id, wig_id, ROW_NUMBER() OVER (PARTITION BY wig_id ORDER BY id) AS rn
    FROM lead_measures
) lm_ranked ON lm_ranked.wig_id = dd.wig_id AND lm_ranked.rn = 3
WHERE dd.lead3 IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM daily_lead_data dld
    WHERE dld.daily_data_id = dd.id AND dld.lead_measure_id = lm_ranked.id
  );

-- lead4 마이그레이션
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value)
SELECT dd.id, lm_ranked.id, dd.lead4
FROM daily_data dd
JOIN (
    SELECT id, wig_id, ROW_NUMBER() OVER (PARTITION BY wig_id ORDER BY id) AS rn
    FROM lead_measures
) lm_ranked ON lm_ranked.wig_id = dd.wig_id AND lm_ranked.rn = 4
WHERE dd.lead4 IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM daily_lead_data dld
    WHERE dld.daily_data_id = dd.id AND dld.lead_measure_id = lm_ranked.id
  );

-- lead5 마이그레이션
INSERT INTO daily_lead_data (daily_data_id, lead_measure_id, lead_value)
SELECT dd.id, lm_ranked.id, dd.lead5
FROM daily_data dd
JOIN (
    SELECT id, wig_id, ROW_NUMBER() OVER (PARTITION BY wig_id ORDER BY id) AS rn
    FROM lead_measures
) lm_ranked ON lm_ranked.wig_id = dd.wig_id AND lm_ranked.rn = 5
WHERE dd.lead5 IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM daily_lead_data dld
    WHERE dld.daily_data_id = dd.id AND dld.lead_measure_id = lm_ranked.id
  );

-- ============================================================
-- 2. weekly_data → weekly_lead_data 마이그레이션
-- ============================================================

-- lead1 마이그레이션
INSERT INTO weekly_lead_data (weekly_data_id, lead_measure_id, lead_value)
SELECT wd.id, lm_ranked.id, wd.lead1
FROM weekly_data wd
JOIN (
    SELECT id, wig_id, ROW_NUMBER() OVER (PARTITION BY wig_id ORDER BY id) AS rn
    FROM lead_measures
) lm_ranked ON lm_ranked.wig_id = wd.wig_id AND lm_ranked.rn = 1
WHERE wd.lead1 IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM weekly_lead_data wld
    WHERE wld.weekly_data_id = wd.id AND wld.lead_measure_id = lm_ranked.id
  );

-- lead2 마이그레이션
INSERT INTO weekly_lead_data (weekly_data_id, lead_measure_id, lead_value)
SELECT wd.id, lm_ranked.id, wd.lead2
FROM weekly_data wd
JOIN (
    SELECT id, wig_id, ROW_NUMBER() OVER (PARTITION BY wig_id ORDER BY id) AS rn
    FROM lead_measures
) lm_ranked ON lm_ranked.wig_id = wd.wig_id AND lm_ranked.rn = 2
WHERE wd.lead2 IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM weekly_lead_data wld
    WHERE wld.weekly_data_id = wd.id AND wld.lead_measure_id = lm_ranked.id
  );

-- lead3 마이그레이션
INSERT INTO weekly_lead_data (weekly_data_id, lead_measure_id, lead_value)
SELECT wd.id, lm_ranked.id, wd.lead3
FROM weekly_data wd
JOIN (
    SELECT id, wig_id, ROW_NUMBER() OVER (PARTITION BY wig_id ORDER BY id) AS rn
    FROM lead_measures
) lm_ranked ON lm_ranked.wig_id = wd.wig_id AND lm_ranked.rn = 3
WHERE wd.lead3 IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM weekly_lead_data wld
    WHERE wld.weekly_data_id = wd.id AND wld.lead_measure_id = lm_ranked.id
  );

-- lead4 마이그레이션
INSERT INTO weekly_lead_data (weekly_data_id, lead_measure_id, lead_value)
SELECT wd.id, lm_ranked.id, wd.lead4
FROM weekly_data wd
JOIN (
    SELECT id, wig_id, ROW_NUMBER() OVER (PARTITION BY wig_id ORDER BY id) AS rn
    FROM lead_measures
) lm_ranked ON lm_ranked.wig_id = wd.wig_id AND lm_ranked.rn = 4
WHERE wd.lead4 IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM weekly_lead_data wld
    WHERE wld.weekly_data_id = wd.id AND wld.lead_measure_id = lm_ranked.id
  );

-- lead5 마이그레이션
INSERT INTO weekly_lead_data (weekly_data_id, lead_measure_id, lead_value)
SELECT wd.id, lm_ranked.id, wd.lead5
FROM weekly_data wd
JOIN (
    SELECT id, wig_id, ROW_NUMBER() OVER (PARTITION BY wig_id ORDER BY id) AS rn
    FROM lead_measures
) lm_ranked ON lm_ranked.wig_id = wd.wig_id AND lm_ranked.rn = 5
WHERE wd.lead5 IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM weekly_lead_data wld
    WHERE wld.weekly_data_id = wd.id AND wld.lead_measure_id = lm_ranked.id
  );

-- ============================================================
-- 3. 검증 쿼리 (마이그레이션 후 확인용)
-- ============================================================

-- 마이그레이션된 daily_lead_data 건수
SELECT 'daily_lead_data 마이그레이션 건수' AS label, COUNT(*) AS cnt FROM daily_lead_data;

-- 마이그레이션된 weekly_lead_data 건수
SELECT 'weekly_lead_data 마이그레이션 건수' AS label, COUNT(*) AS cnt FROM weekly_lead_data;

-- 샘플 확인: daily_lead_data의 첫 10건
SELECT dld.id, dld.daily_data_id, dd.date, lm.name AS lead_measure_name, dld.lead_value
FROM daily_lead_data dld
JOIN daily_data dd ON dd.id = dld.daily_data_id
JOIN lead_measures lm ON lm.id = dld.lead_measure_id
ORDER BY dd.date, lm.id
LIMIT 10;

-- 샘플 확인: weekly_lead_data의 첫 10건
SELECT wld.id, wld.weekly_data_id, wd.week, lm.name AS lead_measure_name, wld.lead_value
FROM weekly_lead_data wld
JOIN weekly_data wd ON wd.id = wld.weekly_data_id
JOIN lead_measures lm ON lm.id = wld.lead_measure_id
ORDER BY wd.week, lm.id
LIMIT 10;

-- ============================================================
-- 4. (선택) 마이그레이션 확인 후 구 컬럼 삭제
-- ⚠️ 반드시 마이그레이션 검증 완료 후 실행!
-- ============================================================
-- ALTER TABLE daily_data DROP COLUMN lead1, DROP COLUMN lead2, DROP COLUMN lead3, DROP COLUMN lead4, DROP COLUMN lead5;
-- ALTER TABLE weekly_data DROP COLUMN lead1, DROP COLUMN lead2, DROP COLUMN lead3, DROP COLUMN lead4, DROP COLUMN lead5;
