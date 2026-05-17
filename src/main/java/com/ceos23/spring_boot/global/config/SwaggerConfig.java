package com.ceos23.spring_boot.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "23기 투표 서비스 API",
                description = "CEOS 23기 투표 서비스 API 문서입니다.",
                version = "v1"
        )
)
@Configuration
public class SwaggerConfig {
}