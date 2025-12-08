package com.boardmate.dto.inquiry;

import com.boardmate.domain.inquiry.Inquiry;
import com.boardmate.domain.inquiry.InquiryStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InquiryResponse {

    private Long id;
    private String title;
    private String content;
    private InquiryStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long userId;
    private String userNickname;

    public static InquiryResponse from(Inquiry inquiry) {
        return InquiryResponse.builder()
                .id(inquiry.getId())
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .status(inquiry.getStatus())
                .createdAt(inquiry.getCreatedAt())
                .updatedAt(inquiry.getUpdatedAt())
                .userId(inquiry.getUser().getId())
                .userNickname(inquiry.getUser().getNickname())
                .build();
    }
}
