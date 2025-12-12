package com.boardmate.dto.meeting;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ApprovedMeetingsResponse {

    private List<MeetingSummary> upcoming;
    private List<MeetingSummary> finished;
    private long totalUpcoming;
    private long totalFinished;
}
