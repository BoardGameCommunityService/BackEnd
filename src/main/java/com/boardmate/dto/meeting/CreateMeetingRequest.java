package com.boardmate.dto.meeting;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateMeetingRequest {

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

    private Long gameId;

    private List<String> tags;  // → JSON 문자열로 저장됨
}
