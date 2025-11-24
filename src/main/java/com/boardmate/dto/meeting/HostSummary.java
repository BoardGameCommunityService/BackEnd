package com.boardmate.dto.meeting;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HostSummary {
    private Long userId;
    private String nickname;
    private String avatarImageUrl;
}