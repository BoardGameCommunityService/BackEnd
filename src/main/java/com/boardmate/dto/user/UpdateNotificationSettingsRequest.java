package com.boardmate.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateNotificationSettingsRequest {
    private Boolean notifyOnMeetingApproved;
    private Boolean notifyOnNewParticipant;
    private Boolean notifyOnInquiryAnswered;
}
