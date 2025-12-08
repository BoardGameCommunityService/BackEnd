package com.boardmate.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationSettingsResponse {
    private Boolean notifyOnMeetingApproved;
    private Boolean notifyOnNewParticipant;
    private Boolean notifyOnInquiryAnswered;
}
