package com.boardmate.dto.meeting;

import com.boardmate.domain.meeting.GenderRestriction;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UpdateMeetingRequest {

    private Long gameId;           // 보드게임

    private String title;
    private String content;

    private String regionCode;
    private String meetingPlace;
    private LocalDateTime meetingAt;

    private Integer maxParticipants;
    private Integer feeEstimate;

    private Double latitude;
    private Double longitude;

    private GenderRestriction genderRestriction;

    private List<String> tags;
}
