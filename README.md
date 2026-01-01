# 🎯 4DX WIG Tracker - Backend

> **4 Disciplines of Execution** 방법론을 기반으로 한 목표 관리 시스템

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)

## 📋 목차

- [프로젝트 소개](#-프로젝트-소개)
- [주요 기능](#-주요-기능)
- [기술 스택](#️-기술-스택)
- [시작하기](#-시작하기)
- [API 문서](#-api-문서)
- [프로젝트 구조](#-프로젝트-구조)
- [ERD](#-erd)
- [개발 가이드](#-개발-가이드)

---

## 🎯 프로젝트 소개

**4DX WIG Tracker**는 조직과 개인의 중요한 목표(WIG: Wildly Important Goals)를 효과적으로 관리하기 위한 백엔드 시스템입니다.

### 4DX란?

4 Disciplines of Execution은 다음 4가지 원칙을 따릅니다:

1. **집중의 원칙** - 가장 중요한 목표에 집중 (최대 2개의 WIG)
2. **선행지표의 원칙** - 결과를 이끄는 활동에 집중
3. **참여 스코어보드의 원칙** - 진행 상황을 명확히 시각화
4. **책임의 리듬 만들기** - 주간 약속과 실행

### 핵심 특징

- ✅ **최소주의 설계** - 필요한 기능만 구현
- ✅ **타입 안정성** - NUMERIC(수치형) / STATE(상태형) 목표 지원
- ✅ **실시간 진행률** - 자동 계산 및 추적
- ✅ **유연한 데이터 관리** - 일간/주간 실적 관리

---

## 🌟 주요 기능

### 1. WIG 관리
- 최대 2개의 WIG 생성 및 관리 (4DX 원칙)
- NUMERIC(수치형) / STATE(상태형) 타입 지원
- 진행률 자동 계산

### 2. Lead Measures (선행지표)
- WIG 달성을 위한 핵심 활동 지표
- 일일/주간 목표 설정
- 실적 추적 및 비교

### 3. Milestones (마일스톤)
- STATE 타입 WIG 전용
- 단계별 진행 상황 관리
- 완료율 자동 계산

### 4. Commitments (주간 약속)
- 주차별 약속 관리
- 완료 상태 토글
- 이행률 추적

### 5. 데이터 추적
- **주간 데이터**: Lag Measure 및 Lead Measure 주간 실적
- **일간 데이터**: Lead Measure 일별 상세 기록

---

## 🛠️ 기술 스택

### Backend
- **Java 21** - 최신 LTS 버전
- **Spring Boot 4.0.0** - 최신 프레임워크
- **Spring Data JPA** - 데이터 액세스
- **Hibernate** - ORM
- **H2 Database** - 개발용 인메모리 DB

### Libraries
- **Lombok** - 보일러플레이트 코드 제거
- **Validation** - 입력 검증
- **Slf4j** - 로깅

---

## 🚀 시작하기

### 설치 및 실행

```bash
# 1. 저장소 클론
git clone https://github.com/stoic-warrior/fdx-backend
cd fdx-backend

# 2. 빌드
./gradlew build

# 3. 실행
./gradlew bootRun
```

> **참고**: Spring Boot 4.0.0, Java 21 사용

### 애플리케이션 접속

- **API 서버**: http://localhost:8080
- **H2 콘솔**: http://localhost:8080/h2-console
    - JDBC URL: `jdbc:h2:mem:fdxdb`
    - Username: `sa`
    - Password: (공백)

---

## 📚 API 문서

### WIG API

#### 전체 WIG 조회
```http
GET /api/wigs
```

**응답 예시:**
```json
[
  {
    "id": 1,
    "title": "백엔드 개발자 취업",
    "fromX": "백수",
    "toY": "취업 성공",
    "byWhen": "2025-12-31",
    "measureType": "STATE",
    "leadMeasures": [...],
    "milestones": [...]
  }
]
```

#### WIG 생성
```http
POST /api/wigs
Content-Type: application/json

{
  "title": "체중 감량",
  "fromX": "75",
  "toY": "68",
  "byWhen": "2025-06-30",
  "measureType": "NUMERIC",
  "unit": "kg"
}
```

#### WIG 개수 조회
```http
GET /api/wigs/count
```

**응답 예시:**
```json
{
  "count": 2,
  "maxCount": 2,
  "canAddMore": false
}
```

### Lead Measures API

#### Lead Measures 조회
```http
GET /api/wigs/{wigId}/lead-measures
```

#### Lead Measure 생성
```http
POST /api/lead-measures
Content-Type: application/json

{
  "name": "코딩 시간",
  "dailyTarget": 6.0,
  "weeklyTarget": 42.0,
  "unit": "시간",
  "wigId": 1
}
```

### Milestones API

#### Milestones 조회
```http
GET /api/wigs/{wigId}/milestones
```

#### 진행률 조회
```http
GET /api/wigs/{wigId}/milestones/progress
```

**응답 예시:**
```json
{
  "total": 5,
  "completed": 2,
  "progressRate": 40.0
}
```

#### 완료 상태 토글
```http
PATCH /api/milestones/{id}/toggle
```

### Commitments API

#### 주차별 약속 조회
```http
GET /api/wigs/{wigId}/commitments/week/{week}
```

#### 이행률 조회
```http
GET /api/wigs/{wigId}/commitments/week/{week}/rate
```

**응답 예시:**
```json
{
  "wigId": 1,
  "week": "W5",
  "total": 4,
  "completed": 2,
  "completionRate": 50.0
}
```

### Weekly Data API

#### 주간 데이터 생성
```http
POST /api/weekly-data
Content-Type: application/json

{
  "week": "W1",
  "milestoneProgress": 20.0,  // STATE 타입용
  "actual": 75.0,              // NUMERIC 타입용
  "target": 74.0,              // NUMERIC 타입용
  "lead1": 35.0,
  "lead2": 2.0,
  "wigId": 1
}
```

### Daily Data API

#### 날짜 범위 조회
```http
GET /api/wigs/{wigId}/daily-data/range?startDate=2025-01-01&endDate=2025-01-07
```

#### 일간 데이터 생성
```http
POST /api/daily-data
Content-Type: application/json

{
  "date": "2025-01-06",
  "week": "W1",
  "dayOfWeek": "월",
  "lead1": 7.0,
  "lead2": 1.0,
  "wigId": 1
}
```

---

## 📁 프로젝트 구조

```
fdx-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/fdx/backend/
│   │   │       ├── domain/
│   │   │       │   ├── wig/
│   │   │       │   │   ├── Wig.java
│   │   │       │   │   ├── WigRepository.java
│   │   │       │   │   ├── WigService.java
│   │   │       │   │   └── WigController.java
│   │   │       │   ├── leadmeasure/
│   │   │       │   ├── milestone/
│   │   │       │   ├── commitment/
│   │   │       │   ├── weeklydata/
│   │   │       │   └── dailydata/
│   │   │       ├── dto/
│   │   │       │   ├── *Request.java
│   │   │       │   └── *Response.java
│   │   │       └── MeasureType.java
│   │   └── resources/
│   │       ├── application.yaml
│   │       └── data.sql
│   └── test/
├── build.gradle
└── README.md
```

---

## 🗄️ ERD

```
┌─────────────┐
│     Wig     │
├─────────────┤
│ id          │
│ title       │
│ fromX       │
│ toY         │
│ byWhen      │
│ measureType │
│ unit        │
└──────┬──────┘
       │
       ├──────────────┐
       │              │
       ▼              ▼
┌─────────────┐  ┌─────────────┐
│LeadMeasure  │  │  Milestone  │
├─────────────┤  ├─────────────┤
│ name        │  │ name        │
│ dailyTarget │  │ completed   │
│weeklyTarget │  │ orderIndex  │
│ unit        │  └─────────────┘
│ wig_id      │
└─────────────┘       
       │
       ├──────────────┬──────────────┐
       ▼              ▼              ▼
┌─────────────┐  ┌──────────┐  ┌──────────┐
│ Commitment  │  │WeeklyData│  │DailyData │
├─────────────┤  ├──────────┤  ├──────────┤
│ text        │  │ week     │  │ date     │
│ week        │  │ actual   │  │ week     │
│ completed   │  │ target   │  │ lead1    │
│ wig_id      │  │ lead1    │  │ lead2    │
└─────────────┘  │ lead2    │  │ wig_id   │
                 │ wig_id   │  └──────────┘
                 └──────────┘
```

### 관계 설명

- **Wig ↔ LeadMeasure**: 1:N (한 WIG에 여러 선행지표)
- **Wig ↔ Milestone**: 1:N (STATE 타입 WIG만)
- **Wig ↔ Commitment**: 1:N (주간 약속)
- **Wig ↔ WeeklyData**: 1:N (주간 실적)
- **Wig ↔ DailyData**: 1:N (일간 실적)

---

## 💻 개발 가이드

### 코드 스타일

#### 1. 네이밍 컨벤션
```java
// Entity
public class Wig { }

// Repository
public interface WigRepository extends JpaRepository<Wig, Long> { }

// Service
public class WigService { }

// Controller
public class WigController { }

// DTO
public class WigRequest { }
public class WigResponse { }
```

#### 2. 패키지 구조
```
도메인별로 패키지 구성 (Domain-Driven Design)
domain/{entity}/
  ├── {Entity}.java
  ├── {Entity}Repository.java
  ├── {Entity}Service.java
  └── {Entity}Controller.java
```

#### 3. 트랜잭션 관리
```java
@Service
@Transactional(readOnly = true)  // 기본은 읽기 전용
public class WigService {
    
    @Transactional  // 쓰기 작업만 트랜잭션 활성화
    public WigResponse createWig(WigRequest request) {
        // ...
    }
}
```

### 빌드 및 테스트

```bash
# 빌드
./gradlew build

# 테스트 실행
./gradlew test

# 클린 빌드
./gradlew clean build

# 실행
./gradlew bootRun
```

### H2 콘솔 사용법

1. 브라우저에서 http://localhost:8080/h2-console 접속
2. JDBC URL 입력: `jdbc:h2:mem:fdxdb`
3. Connect 클릭
4. SQL 쿼리 실행 예시:
```sql
-- 모든 WIG 조회
SELECT * FROM wigs;

-- 특정 WIG의 Lead Measures 조회
SELECT * FROM lead_measures WHERE wig_id = 1;

-- 완료된 Milestones 조회
SELECT * FROM milestones WHERE completed = true;
```

### 테스트 데이터

애플리케이션 시작 시 `data.sql`이 자동 실행되어 다음 데이터가 생성됩니다:

- **WIG 2개** (백엔드 개발자 취업, 체중 감량)
- **Lead Measures 4개**
- **Milestones 5개**
- **Commitments 4개**
- **Weekly Data 8개**
- **Daily Data 10개**

---

