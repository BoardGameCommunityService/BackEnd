package com.boardmate.dto.meeting;

import com.boardmate.domain.meeting.Meeting;
import com.boardmate.domain.meeting.MeetingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class MeetingDetailResponse {

    private Long meetingId;

    private String title;
    private String content;

    private Long hostId;
    private String hostNickname;


    private String regionText;
    private LocalDateTime meetingAt;

    private String meetingPlace;
    private Double latitude;
    private Double longitude;

    private Integer maxParticipants;
    private Integer currentParticipants;
    private Integer feeEstimate;

    private String genderRestriction;

    private String gameName;
    private Long gameId;

    private List<String> tags;

    private MeetingStatus status;

    private String myStatus;

    private List<String> participantNicknames;

    public static MeetingDetailResponse of(
            Meeting meeting,
            int currentParticipants,
            String regionText,
            String gameName,
            List<String> tags,
            String myStatus,
            List<String> participantNicknames
    ) {
        return MeetingDetailResponse.builder()
                .meetingId(meeting.getId())
                .title(meeting.getTitle())
                .content(meeting.getContent())
                .hostId(meeting.getHost().getId())
                .hostNickname(meeting.getHost().getNickname())
                .regionText(regionText)
                .meetingAt(meeting.getMeetingAt())
                .meetingPlace(meeting.getMeetingPlace())
                .latitude(meeting.getLatitude())
                .longitude(meeting.getLongitude())
                .maxParticipants(meeting.getMaxParticipants())
                .currentParticipants(currentParticipants)
                .feeEstimate(meeting.getFeeEstimate())
                .genderRestriction(
                        meeting.getGenderRestriction() != null
                                ? meeting.getGenderRestriction().name()
                                : null
                )
                .gameName(gameName)
                .gameId(meeting.getGame() != null ? meeting.getGame().getId() : null)
                .tags(tags)
                .status(meeting.getStatus())
                .myStatus(myStatus)
                .participantNicknames(participantNicknames)
                .build();
    }
}
