package com.boardmate.dto.auth;

import lombok.Data;

@Data
public class CompleteSignupRequest {
    private String nickname;
    private String gender; // "MALE" / "FEMALE"
    private String region; // "서울특별시 강남구"
    private Consent consent; // 약관 동의 객체

    @Data
    public static class Consent {
        private Boolean service; // 서비스 이용약관 (필수)
        private Boolean privacy; // 개인정보 처리방침 (필수)
        private Boolean location; // 위치기반 서비스 (선택)
    }
}
