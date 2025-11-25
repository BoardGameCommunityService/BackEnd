package com.boardmate.dto.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportRequest {
    private String targetType; // USER, POST, COMMENT 등
    private Long targetId;
    private String reason;
    private String details;
}
