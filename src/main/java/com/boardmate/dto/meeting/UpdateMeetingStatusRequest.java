package com.boardmate.dto.meeting;

import com.boardmate.domain.meeting.MeetingStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMeetingStatusRequest {
    private MeetingStatus status;
}
