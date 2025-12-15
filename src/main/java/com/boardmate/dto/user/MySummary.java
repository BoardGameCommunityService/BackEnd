package com.boardmate.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MySummary {
    private Long userId;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private String region;

    // 모임 관련
    private long hostCount; // 내가 만든 모임 수
    private long approvedCount; // 승인된 모임 수
    private long pendingCount; // 신청 대기 모임 수

    // 알림 관련
    private boolean hasNewNotifications; // 알림 여부
}
