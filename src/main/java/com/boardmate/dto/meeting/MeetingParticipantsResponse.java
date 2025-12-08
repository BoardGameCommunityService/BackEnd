package com.boardmate.dto.meeting;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MeetingParticipantsResponse {
    private Integer currentParticipants; // includes host and approved participants
    private List<ParticipantSummary> participants;
}
