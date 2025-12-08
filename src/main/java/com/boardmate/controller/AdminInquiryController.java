package com.boardmate.controller;

import com.boardmate.domain.inquiry.Inquiry;
import com.boardmate.domain.inquiry.InquiryStatus;
import com.boardmate.domain.user.User;
import com.boardmate.dto.inquiry.AnswerInquiryRequest;

import com.boardmate.dto.inquiry.InquiryResponse;
import com.boardmate.repository.InquiryRepository;
import com.boardmate.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/inquiries")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")   // 클래스 레벨로 걸어도 됨
public class AdminInquiryController {

    private final InquiryService inquiryService;
    private final InquiryRepository inquiryRepository;

    // 전체 문의 리스트 (관리자용)
    @GetMapping
    public ResponseEntity<Page<InquiryResponse>> getInquiries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) InquiryStatus status
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Inquiry> result;
        if (status != null) {
            result = inquiryRepository.findByStatus(status, pageable);
        } else {
            result = inquiryRepository.findAll(pageable);
        }

        Page<InquiryResponse> mapped = result.map(InquiryResponse::from);
        return ResponseEntity.ok(mapped);
    }

    // 단일 문의 상세 (관리자용)
    @GetMapping("/{id}")
    public ResponseEntity<InquiryResponse> getInquiryDetail(
            @PathVariable Long id
    ) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inquiry not found"));

        return ResponseEntity.ok(InquiryResponse.from(inquiry));
    }

    // 답변 작성 / 수정
    @PostMapping("/{id}/answer")
    public ResponseEntity<InquiryResponse> answerInquiry(
            @AuthenticationPrincipal User admin,
            @PathVariable Long id,
            @RequestBody AnswerInquiryRequest req
    ) {
        InquiryResponse response =
                inquiryService.answerInquiry(admin.getId(), id, req);
        return ResponseEntity.ok(response);
    }

}
