package com.boardmate.dto.auth;

import lombok.Data;

@Data
public class SocialLoginRequest {
    private String provider;         // "kakao" | "google"
    private String socialId;         // providerAccountId
    private String email;
    private String nickname;         // 소셜 닉네임
    private String profileImageUrl;
}
