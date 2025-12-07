package com.boardmate.dto.user;

import com.boardmate.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoResponse {
    private Long userId;
    private String email;
    private String nickname;
    private String gender;
    private String region;
    private String profileImageUrl;
    private String provider;

    public static UserInfoResponse from(User user) {
        return UserInfoResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .region(user.getRegion())
                .profileImageUrl(user.getProfileImageUrl())
                .provider(user.getProvider())
                .build();
    }
}
