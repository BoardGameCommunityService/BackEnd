package com.boardmate.dto.meeting;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateMeetingRequest {

    private Long meetingId;

    private String title;
    private String content;

    // multiple selectable game names
    private List<String> gameNames;

    private String meetingPlace;
    private String meetingAddress;
    private String regionCode;
    private LocalDateTime meetingAt;

    // null means unlimited
    private Integer maxParticipants;
}
