# ==========================================
# 1단계: 빌드 스테이지
# ==========================================
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

# Gradle 캐싱을 위해 의존성 파일 먼저 복사
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# 의존성만 먼저 다운로드 (캐싱 활용)
RUN gradle dependencies --no-daemon || true

# 소스 코드 복사
COPY src ./src

# JAR 빌드 (테스트 스킵)
RUN gradle bootJar --no-daemon -x test

# ==========================================
# 2단계: 실행 스테이지
# ==========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 보안: root가 아닌 일반 사용자로 실행
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# 빌드된 JAR 복사 (build.gradle에서 fdx-backend.jar로 고정)
COPY --from=builder /app/build/libs/fdx-backend.jar app.jar

# 소유권 변경
RUN chown -R appuser:appgroup /app

# 일반 사용자로 전환
USER appuser

# 포트 노출
EXPOSE 8080

# 헬스체크
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# 실행 (프로파일은 환경변수로 지정)
ENTRYPOINT ["java", "-jar", "app.jar"]