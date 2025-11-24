package com.boardmate.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SocialLoginResponse {

    private Long userId;
    private String email;
    private String nickname;
    private String role;
    private boolean profileCompleted;  // 추가 정보 입력 여부

    // 기존 로그인 로직을 위해 필드는 그대로 두고 사용할지 말지만 선택.
    private String accessToken;
    private String refreshToken;
    private long accessTokenExpiresAt;
}
