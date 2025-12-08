package com.boardmate.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "소셜 로그인 응답 DTO (Spring Boot → NextAuth)")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SocialLoginResponse {

    @Schema(description = "오류 코드 (ACCOUNT_DEACTIVATED 등)")
    private String errorCode;

    @Schema(description = "오류 메시지")
    private String errorMessage;

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

    // 성공 응답용 생성자 (기존)
    public SocialLoginResponse(Long userId, String email, String nickname, String role,
            boolean profileCompleted, String accessToken, String refreshToken,
            long accessTokenExpiresAt, boolean alreadyRegistered) {
        this.errorCode = null;
        this.errorMessage = null;
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
        this.profileCompleted = profileCompleted;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
        this.alreadyRegistered = alreadyRegistered;
    }

    // 에러 응답용 정적 팩토리 메서드
    public static SocialLoginResponse error(String errorCode, String errorMessage) {
        SocialLoginResponse response = new SocialLoginResponse();
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        return response;
    }
}
