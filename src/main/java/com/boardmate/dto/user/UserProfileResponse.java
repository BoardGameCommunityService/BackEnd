package com.boardmate.dto.user;

import com.boardmate.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponse {

    private Long userId;
    private String email;
    private String nickname;
    private String gender;
    private String region;
    private String profileImageUrl;
    private boolean profileCompleted;

    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .region(user.getRegion())
                .profileImageUrl(user.getProfileImageUrl())
                .profileCompleted(user.isProfileCompleted())
                .build();
    }
}
