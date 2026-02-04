-- MySQL 초기화 스크립트
-- 한글 지원을 위한 설정

-- 데이터베이스 캐릭터셋 확인/변경
ALTER DATABASE fdxdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 권한 부여
GRANT ALL PRIVILEGES ON fdxdb.* TO 'fdxuser'@'%';
FLUSH PRIVILEGES;