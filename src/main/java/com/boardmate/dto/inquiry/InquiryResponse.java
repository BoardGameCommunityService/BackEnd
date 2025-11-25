package com.boardmate.dto.inquiry;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InquiryResponse {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String answer;
    private Long answeredBy;
    private LocalDateTime createdAt;
    private LocalDateTime answeredAt;
}
