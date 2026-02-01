package com.fdx.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI 설정
 * API 문서 자동 생성 및 테스트 UI 제공
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("4DX WIG Tracker API")
                        .version("1.0.0")
                        .description("""
                                # 4 Disciplines of Execution - WIG Tracker
                                
                                가장 중요한 목표(WIG)를 효과적으로 관리하기 위한 REST API
                                
                                ## 핵심 기능
                                - **WIG 관리**: 최대 2개의 중요한 목표 설정 및 추적
                                - **Lead Measures**: 목표 달성을 위한 선행지표 관리
                                - **Milestones**: STATE 타입 목표의 단계별 진행 관리
                                - **Commitments**: 주간 약속 및 이행률 추적
                                - **Data Tracking**: 일간/주간 실적 데이터 기록
                                
                                ## 인증
                                1. /api/auth/login으로 로그인
                                2. 받은 accessToken을 Authorize 버튼에 입력
                                3. 형식: Bearer {토큰}
                                """)
                        .contact(new Contact()
                                .name("4DX Team")
                                .email("contact@fdx.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("개발 서버"),
                        new Server()
                                .url("https://api.fdx.com")
                                .description("운영 서버 (예시)")
                ))
                // JWT 인증 설정 추가
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT 토큰을 입력하세요 (Bearer 제외)")));
    }
}