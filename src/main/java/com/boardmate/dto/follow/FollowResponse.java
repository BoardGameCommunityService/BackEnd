package com.boardmate.dto.follow;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FollowResponse {
    private Long id;
    private Long followerId;
    private Long followeeId;
    private LocalDateTime createdAt;
}
