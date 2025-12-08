package com.boardmate.dto.meeting;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateParticipantStatusRequest {
    private String status;  // "APPROVED" / "REJECTED"
}
