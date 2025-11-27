package com.boardmate.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "소셜 로그인 응답 DTO (Spring Boot → NextAuth)")
@Data
@AllArgsConstructor
public class SocialLoginResponse {

    @Schema(description = "DB 사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "닉네임", example = "보드러버")
    private String nickname;

    @Schema(description = "권한", example = "USER")
    private String role;

    @Schema(description = "추가 정보 입력 여부", example = "false")
    private boolean profileCompleted;

    @Schema(description = "(구버전) 액세스 토큰", example = "null")
    private String accessToken;

    @Schema(description = "(구버전) 리프레시 토큰", example = "null")
    private String refreshToken;

    @Schema(description = "(구버전) 액세스 토큰 만료 시각", example = "0")
    private long accessTokenExpiresAt;

    @Schema(description = "이미 등록된 사용자 여부", example = "true")
    private boolean alreadyRegistered;
}
