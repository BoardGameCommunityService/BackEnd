package com.boardmate.dto.notification;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NotificationsResponse {
    private List<NotificationItem> items;
    private long total;
}
