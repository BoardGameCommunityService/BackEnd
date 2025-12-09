package com.boardmate.dto.meeting;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyParticipationSummary {
    private long hostCount;
    private long approvedCount;
    private long pendingCount;
}
