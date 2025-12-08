package com.boardmate.dto.inquiry;

import lombok.Data;

@Data
public class CreateInquiryRequest {

    private String title;
    private String content;

    // 필요하면 category, osInfo, appVersion 같은 필드도 추가 가능
    // private String category;
}
