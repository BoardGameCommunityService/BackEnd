package com.boardmate.dto.meeting;

import com.boardmate.domain.meeting.Meeting;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MeetingCardResponse {

    private Long meetingId;

    private String title;

    private String regionText;       // 예: "서울, 노원구"
    private LocalDateTime meetingAt; // 모임 시간

    private int currentParticipants; // 승인 인원
    private Integer maxParticipants; // 정원

    private String genderRestriction; // ALL / MALE_ONLY / FEMALE_ONLY

    private String gameName;         // 대표 게임명
    private List<String> tags;       // 태그 텍스트들

    private Integer likeCount;
    private Integer commentCount;

    public static MeetingCardResponse from(
            Meeting meeting,
            int currentParticipants,
            String regionText,
            String gameName,
            List<String> tags
    ) {
        return MeetingCardResponse.builder()
                .meetingId(meeting.getId())
                .title(meeting.getTitle())
                .regionText(regionText)
                .meetingAt(meeting.getMeetingAt())
                .currentParticipants(currentParticipants)
                .maxParticipants(meeting.getMaxParticipants())
                .genderRestriction(
                        meeting.getGenderRestriction() != null
                                ? meeting.getGenderRestriction().name()
                                : null
                )
                .gameName(gameName)
                .tags(tags)
                .likeCount(meeting.getLikeCount())
                .commentCount(meeting.getCommentCount())
                .build();
    }
}
