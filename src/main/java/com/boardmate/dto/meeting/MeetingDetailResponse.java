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
    private String ruleLevel;

    private String regionCode;
    private String meetingPlace;
    private LocalDateTime meetingAt;

    private Integer maxParticipants;
    private Integer currentParticipants;
    private Integer feeEstimate;

    private String status;

    private String tagsJson;

    private HostSummary host;
}
