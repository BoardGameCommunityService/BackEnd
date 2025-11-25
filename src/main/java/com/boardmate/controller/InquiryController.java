package com.boardmate.controller;

import com.boardmate.domain.user.User;
import com.boardmate.dto.inquiry.AnswerInquiryRequest;
import com.boardmate.dto.inquiry.CreateInquiryRequest;
import com.boardmate.dto.inquiry.InquiryResponse;
import com.boardmate.service.InquiryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "문의", description = "문의 관련 API")
@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @Operation(summary = "문의 등록", description = "새로운 문의를 등록합니다. JWT 인증이 필요합니다.")
    @ApiResponse(responseCode = "200", description = "문의 등록 성공")
    @PostMapping
    public ResponseEntity<?> createInquiry(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @RequestBody CreateInquiryRequest request) {
        Long inquiryId = inquiryService.createInquiry(user.getId(), request);
        return ResponseEntity.ok(Map.of("inquiryId", inquiryId, "message", "문의가 등록되었습니다."));
    }

    @Operation(summary = "내 문의 목록", description = "내가 등록한 문의 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/my")
    public ResponseEntity<List<InquiryResponse>> getMyInquiries(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(inquiryService.getMyInquiries(user.getId()));
    }

    // TODO: 관리자 권한 체크 로직 추가 필요 (회의 때 논의 필요)
    @Operation(summary = "전체 문의 목록 (관리자)", description = "모든 문의 목록을 조회합니다. ADMIN 권한이 필요합니다. ADMIN 권한 체크 로직 추가 필요 (회의 때 논의 필요)")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<InquiryResponse>> getAllInquiries() {
        // TODO: ADMIN 권한 체크 추가
        return ResponseEntity.ok(inquiryService.getAllInquiries());
    }

    @Operation(summary = "문의 답변 (관리자)", description = "문의에 답변을 등록합니다. ADMIN 권한이 필요합니다. ADMIN 권한 체크 로직 추가 필요 (회의 때 논의 필요)")
    @ApiResponse(responseCode = "200", description = "답변 등록 성공")
    @PostMapping("/{inquiryId}/answer")
    public ResponseEntity<?> answerInquiry(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "문의 ID", example = "1") @PathVariable Long inquiryId,
            @RequestBody AnswerInquiryRequest request) {
        // TODO: ADMIN 권한 체크 추가
        inquiryService.answerInquiry(inquiryId, user.getId(), request.getAnswer());
        return ResponseEntity.ok(Map.of("message", "답변이 등록되었습니다."));
    }
}
