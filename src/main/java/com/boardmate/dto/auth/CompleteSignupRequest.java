package com.boardmate.dto.auth;

import lombok.Data;

@Data
public class CompleteSignupRequest {
    private String nickname;
    private String gender;   // "MALE" / "FEMALE"
    private String region;   // "서울특별시 강남구"
}
