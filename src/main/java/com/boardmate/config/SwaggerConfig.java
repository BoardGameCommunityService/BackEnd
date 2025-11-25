package com.boardmate.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        Components components = new Components()
                .addSecuritySchemes(jwt, new SecurityScheme()
                        .name(jwt)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .components(components)
                .info(apiInfo())
                .addSecurityItem(securityRequirement);
    }

    private Info apiInfo() {
        return new Info()
                .title("BoardMate API")
                .description("보드게임 커뮤니티 플랫폼 API 문서")
                .version("1.0.0");
    }

    @Bean
    public OpenApiCustomizer customOpenAPI() {
        List<String> tagOrder = List.of(
                "테스트",
                "인증",
                "모집",
                "모집참가",
                "팔로우",
                "차단",
                "신고",
                "문의",
                "시스템");

        return openApi -> openApi.setTags(
                openApi.getTags().stream()
                        .sorted(Comparator.comparingInt(tag -> IntStream.range(0, tagOrder.size())
                                .filter(i -> tag.getName().contains(tagOrder.get(i)))
                                .findFirst()
                                .orElse(tagOrder.size())))
                        .toList());
    }
}
