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
                .description("보드게임 커뮤니티 플랫폼 API 문서"
                        + "<br><br>" +
                        "/api/auth/sync-from-nextauth : NextAuth가 처음 로그인 후 유저를 동기화하기 위해 부르는 API입니다.(JWT 포함 반환)"
                        + "<br><br>" +
                        "해당 API는 메일 주소 기반으로 사용자를 식별하며, 이미 가입된 사용자라면 기존 정보를 반환합니다. 신규 사용자라면 DB에 저장 후 정보를 반환합니다."
                        + "<br><br>" +
                        "모든 API는 JWT 인증이 필요합니다(테스트, 시스템 제외)"
                        + "<br><br>" +
                        "Swagger UI 우측 상단의 'Authorize' 버튼을 클릭하여 발급받은 JWT 토큰을 입력해야 합니다.")
                .version("1.0.0");
    }

    @Bean
    public OpenApiCustomizer customOpenAPI() {
        List<String> tagOrder = List.of(
                "테스트",
                "인증",
                "사용자",
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
