package com.boardmate.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    private String nickname;  // 필수
    private String gender;    // "MALE" / "FEMALE" 등 String으로 관리 중
    private String region;    // "서울특별시 강남구" 등
    private String profileImageUrl;
}
