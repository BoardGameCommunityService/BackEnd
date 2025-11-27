package com.boardmate.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "소셜 로그인 요청 DTO (NextAuth → Spring Boot)")
public class SocialLoginRequest {
    @Schema(description = "소셜 로그인 제공자", example = "kakao")
    private String provider;

    @Schema(description = "소셜 계정 고유 ID (providerAccountId)", example = "1234567890")
    private String socialId;

    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String email;

    @Schema(description = "소셜 닉네임", example = "보드러버")
    private String nickname;

    @Schema(description = "프로필 이미지 URL", example = "https://k.kakaocdn.net/dn/...jpg")
    private String profileImageUrl;
}
