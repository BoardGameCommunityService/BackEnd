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

    private String accessToken;
    private String refreshToken;
    private long accessTokenExpiresAt;
}
