package com.boardmate.controller;

import com.boardmate.domain.user.User;
import com.boardmate.dto.report.CreateReportRequest;
import com.boardmate.dto.report.ReportResponse;
import com.boardmate.dto.report.UpdateReportStatusRequest;
import com.boardmate.service.ReportService;
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

@Tag(name = "신고", description = "신고 관련 API")
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "신고 등록", description = "사용자 또는 게시글(모집글)을 신고합니다. JWT 인증이 필요합니다.\n\n" +
            "**신고 가능한 대상 타입(targetType):**\n" +
            "- USER: 사용자 신고\n" +
            "- MEETING: 게시글(모집글) 신고\n\n" +
            "**처리 상태(status):**\n" +
            "- PENDING: 대기중\n" +
            "- PROCESSING: 처리중\n" +
            "- COMPLETED: 완료\n" +
            "- REJECTED: 반려")
    @ApiResponse(responseCode = "200", description = "신고 성공")
    @PostMapping
    public ResponseEntity<?> createReport(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @RequestBody CreateReportRequest request) {
        Long reportId = reportService.createReport(user.getId(), request);
        return ResponseEntity.ok(Map.of("reportId", reportId, "message", "신고가 접수되었습니다."));
    }

    @Operation(summary = "내 신고 목록", description = "내가 한 신고 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/my")
    public ResponseEntity<List<ReportResponse>> getMyReports(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(reportService.getMyReports(user.getId()));
    }

    @Operation(summary = "전체 신고 목록 (관리자)", description = "모든 신고 목록을 조회합니다. ADMIN 권한이 필요합니다. ADMIN 권한 체크 로직 추가 필요 (회의 때 논의 필요)")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        // TODO: ADMIN 권한 체크 추가
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @Operation(summary = "신고 상태 변경 (관리자)", description = "신고 처리 상태를 변경합니다. ADMIN 권한이 필요합니다. ADMIN 권한 체크 로직 추가 필요 (회의 때 논의 필요)")
    @ApiResponse(responseCode = "200", description = "상태 변경 성공")
    @PatchMapping("/{reportId}/status")
    public ResponseEntity<?> updateStatus(
            @Parameter(description = "신고 ID", example = "1") @PathVariable Long reportId,
            @RequestBody UpdateReportStatusRequest request) {
        // TODO: ADMIN 권한 체크 추가
        reportService.updateStatus(reportId, request.getStatus());
        return ResponseEntity.ok(Map.of("message", "상태가 변경되었습니다."));
    }
}
