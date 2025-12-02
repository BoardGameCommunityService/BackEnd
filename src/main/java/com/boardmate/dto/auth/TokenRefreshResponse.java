package com.boardmate.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "토큰 재발급 응답")
public class TokenRefreshResponse {

    @Schema(description = "오류 코드 (REFRESH_TOKEN_NOT_FOUND, INVALID_REFRESH_TOKEN 등)")
    private String errorCode;

    @Schema(description = "새로 발급된 액세스 토큰")
    private String accessToken;

    @Schema(description = "새로 발급된 리프레시 토큰 (HttpOnly 쿠키로 전달되므로 응답에서는 null)")
    private String refreshToken;

    @Schema(description = "액세스 토큰 만료 시각")
    private Long accessTokenExpiresAt;

    // 성공 응답용 생성자
    public TokenRefreshResponse(String accessToken, String refreshToken, Long accessTokenExpiresAt) {
        this.errorCode = null;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresAt = accessTokenExpiresAt;
    }

    // 에러 응답용 정적 팩토리 메서드
    public static TokenRefreshResponse error(String errorCode) {
        TokenRefreshResponse response = new TokenRefreshResponse();
        response.setErrorCode(errorCode);
        return response;
    }
}
