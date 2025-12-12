package com.boardmate.dto.meeting;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MeetingSummary {

    private Long meetingId;

    private String title;
    private String content;
    private String meetingPlace;
    private String meetingAddress;
    private String regionCode;
    private LocalDateTime meetingAt;

    private Integer maxParticipants;
    private Integer currentParticipants;

    private String status;

    private String gameNamesJson;

    private HostSummary host;
}
