# 4DX Backend - WIG Tracker API

4 Disciplines of Execution 기반 목표 관리 시스템 백엔드

## 📋 프로젝트 구조

```
src/main/java/com/fdx/backend/
├── domain/
│   ├── MeasureType.java          # 측정 유형 ENUM
│   └── wig/
│       ├── Wig.java              # WIG 엔티티
│       ├── WigRepository.java    # 데이터 액세스 계층
│       ├── WigService.java       # 비즈니스 로직
│       └── WigController.java    # REST API 컨트롤러
└── dto/
    ├── WigRequest.java           # API 요청 DTO
    └── WigResponse.java          # API 응답 DTO

src/main/resources/
├── application.yml               # 애플리케이션 설정
└── data.sql                      # 초기 테스트 데이터
```

## 🚀 실행 방법

### 1. IntelliJ IDEA에서 실행
1. 프로젝트를 IntelliJ로 엽니다
2. Gradle 의존성이 자동으로 다운로드될 때까지 기다립니다
3. `src/main/java/com/fdx/backend/FdxBackendApplication.java` 파일을 찾습니다
4. 파일을 열고 `main` 메서드 옆의 ▶️ 버튼을 클릭합니다

### 2. 터미널에서 실행
```bash
# Windows
gradlew.bat bootRun

# Mac/Linux
./gradlew bootRun
```

### 3. 실행 확인
- 콘솔에 "Started FdxBackendApplication" 메시지가 보이면 성공!
- 브라우저에서 http://localhost:8080 접속

## 🗄️ H2 데이터베이스 콘솔

개발 중 데이터베이스를 직접 확인할 수 있습니다:

1. 브라우저에서 http://localhost:8080/h2-console 접속
2. 다음 정보로 로그인:
   - JDBC URL: `jdbc:h2:mem:fdxdb`
   - User Name: `sa`
   - Password: (비워두기)

## 📡 API 엔드포인트

### 1. 모든 WIG 조회
```bash
GET http://localhost:8080/api/wigs
```

### 2. 특정 WIG 조회
```bash
GET http://localhost:8080/api/wigs/1
```

### 3. 유형별 WIG 조회
```bash
# 수치형 목표 조회
GET http://localhost:8080/api/wigs/type/NUMERIC

# 상태형 목표 조회
GET http://localhost:8080/api/wigs/type/STATE
```

### 4. WIG 검색
```bash
GET http://localhost:8080/api/wigs/search?keyword=취업
```

### 5. WIG 생성
```bash
POST http://localhost:8080/api/wigs
Content-Type: application/json

{
  "title": "운동 습관 만들기",
  "fromX": "운동 안함",
  "toY": "주 5회 운동",
  "byWhen": "2025-06-30",
  "measureType": "STATE"
}
```

### 6. WIG 수정
```bash
PUT http://localhost:8080/api/wigs/1
Content-Type: application/json

{
  "title": "백엔드 개발자 취업 (수정)",
  "fromX": "백수",
  "toY": "대기업 합격",
  "byWhen": "2025-12-31",
  "measureType": "STATE"
}
```

### 7. WIG 삭제
```bash
DELETE http://localhost:8080/api/wigs/1
```

## 🧪 API 테스트 방법

### 방법 1: 브라우저 (GET 요청만 가능)
```
http://localhost:8080/api/wigs
```

### 방법 2: Postman 사용
1. Postman 다운로드: https://www.postman.com/downloads/
2. 위의 API 엔드포인트를 Postman에 입력하여 테스트

### 방법 3: IntelliJ HTTP Client
1. IntelliJ에서 `test-api.http` 파일 생성
2. 다음 내용 작성:

```http
### 모든 WIG 조회
GET http://localhost:8080/api/wigs

### WIG 생성
POST http://localhost:8080/api/wigs
Content-Type: application/json

{
  "title": "독서 습관 만들기",
  "fromX": "0",
  "toY": "12",
  "byWhen": "2025-12-31",
  "measureType": "NUMERIC",
  "unit": "권"
}

### WIG 조회
GET http://localhost:8080/api/wigs/1

### WIG 수정
PUT http://localhost:8080/api/wigs/1
Content-Type: application/json

{
  "title": "독서 습관 만들기 (수정)",
  "fromX": "0",
  "toY": "24",
  "byWhen": "2025-12-31",
  "measureType": "NUMERIC",
  "unit": "권"
}

### WIG 삭제
DELETE http://localhost:8080/api/wigs/1
```

3. 각 요청 옆의 ▶️ 버튼 클릭하여 실행

### 방법 4: curl (터미널)
```bash
# 조회
curl http://localhost:8080/api/wigs

# 생성
curl -X POST http://localhost:8080/api/wigs \
  -H "Content-Type: application/json" \
  -d '{"title":"새 목표","fromX":"0","toY":"100","byWhen":"2025-12-31","measureType":"NUMERIC","unit":"개"}'
```

## 📚 다음 단계

현재는 WIG의 기본 CRUD만 구현되어 있습니다. 다음 단계로 구현할 기능:

1. **Lead Measures (선행지표)** - WIG와 1:N 관계
2. **Weekly/Daily Data (주간/일일 데이터)** - 진행도 추적
3. **Milestones (마일스톤)** - 상태형 WIG의 체크리스트
4. **Commitments (주간 약속)** - 주간 실행 계획
5. **통계 API** - 진행률, 달성도 계산
6. **Spring Security** - 인증/인가
7. **MySQL 연동** - 프로덕션 DB

## 🐛 트러블슈팅

### 포트 8080이 이미 사용 중
- `application.yml`에서 `server.port: 8081`로 변경

### Gradle 빌드 실패
```bash
# Gradle Wrapper 권한 부여 (Mac/Linux)
chmod +x gradlew

# 의존성 다시 다운로드
./gradlew clean build --refresh-dependencies
```

### H2 콘솔 접속 안됨
- `application.yml`에서 `spring.h2.console.enabled: true` 확인

## 📞 문의

궁금한 점이 있으면 Issues에 등록해주세요!
