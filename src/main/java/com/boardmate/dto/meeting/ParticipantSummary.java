package com.boardmate.dto.meeting;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParticipantSummary {
    private Long userId;
    private String nickname;
    private String avatarImageUrl;
    private String status; // PENDING | APPROVED | DENIED
}
