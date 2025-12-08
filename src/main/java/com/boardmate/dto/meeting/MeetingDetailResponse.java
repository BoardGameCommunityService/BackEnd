package com.boardmate.dto.meeting;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MeetingDetailResponse {

    private Long meetingId;

    private String title;
    private String content;
    private String meetingPlace;
    private LocalDateTime meetingAt;

    private Integer maxParticipants; // null = unlimited
    private Integer currentParticipants;

    private String status;

    private String gameNamesJson;

    private HostSummary host;
}
