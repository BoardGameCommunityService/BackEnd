package com.boardmate.controller;

import com.boardmate.domain.user.User;
import com.boardmate.dto.inquiry.CreateInquiryRequest;
import com.boardmate.dto.inquiry.InquiryResponse;
import com.boardmate.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    /**
     * 문의 생성
     * POST /api/inquiries
     */
    @PostMapping
    public ResponseEntity<InquiryResponse> createInquiry(
            @AuthenticationPrincipal User currentUser,
            @RequestBody CreateInquiryRequest req
    ) {
        InquiryResponse response =
                inquiryService.create(currentUser.getId(), req);
        return ResponseEntity.ok(response);
    }

    /**
     * 내가 남긴 문의 목록
     * GET /api/inquiries/me?page=0&size=10
     */
    @GetMapping("/me")
    public ResponseEntity<Page<InquiryResponse>> getMyInquiries(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<InquiryResponse> result =
                inquiryService.getMyInquiries(currentUser.getId(), page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * (옵션) 개별 문의 상세
     * GET /api/inquiries/me/{id}
     */
    @GetMapping("/me/{id}")
    public ResponseEntity<InquiryResponse> getMyInquiryDetail(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id
    ) {
        InquiryResponse response =
                inquiryService.getMyInquiryDetail(currentUser.getId(), id);
        return ResponseEntity.ok(response);
    }
}
