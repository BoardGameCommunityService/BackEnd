package com.boardmate.dto.notification;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationItem {
    private Long id; // null for dynamic
    private String type; // e.g., REGION_MEETING, ANSWER, ...
    private String title;
    private String message;
    private Long resourceId;
    private Long relatedUserId; // 신청자 ID
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
