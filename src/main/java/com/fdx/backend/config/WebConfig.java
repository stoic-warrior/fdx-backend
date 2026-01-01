package com.fdx.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS 설정
 * React 프론트엔드와의 통신을 위한 Cross-Origin 허용
 */
@Configuration // 스프링의 설정파일로 등록, 전역 법률
public class WebConfig implements WebMvcConfigurer { // Spring MVC 설정을 커스터마이징 할 수 있는 인터페이스

    @Override
    public void addCorsMappings(CorsRegistry registry) { // 브라우저는 서버와 다른 origin이 응답을 보지 못하게 한다.(SOP) 백엔드(8080)와 다른 출신(origin)들이 서버 응답을 볼 수 있게 하는 허가증 = CORS에 등록.
        // CORS = 브라우저의 다른 악성사이트에서, 이 서버의 응답을 열람하지 못하게 하는 역할

        registry.addMapping("/api/**")
                .allowedOrigins(
                        "http://localhost:3000",  // React 개발 서버
                        "http://localhost:5173"   // Vite 개발 서버
                )
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600); // 1시간 동안 preflight 결과 캐싱. 1시간동안 예비검문 안보내도 됨
    }
}
