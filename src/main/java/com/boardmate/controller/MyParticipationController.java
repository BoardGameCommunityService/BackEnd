package com.boardmate.controller;

import com.boardmate.domain.user.User;
import com.boardmate.dto.meeting.ApprovedMeetingsResponse;
import com.boardmate.dto.meeting.MeetingDetailResponse;
import com.boardmate.dto.meeting.MyParticipationSummary;
import com.boardmate.service.MeetingParticipantService;
import com.boardmate.service.MeetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "내 모임", description = "내가 만든 모임 및 참가 상태별 조회 API")
@RestController
@RequestMapping("/api/my/participations")
@RequiredArgsConstructor
public class MyParticipationController {

    private final MeetingParticipantService participantService;
    private final MeetingService meetingService;

    @Operation(summary = "내가 만든 모임 목록", description = "호스트가 만든 모임 목록을 조회합니다. JWT 인증 필요. 페이징 지원.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/host/meetings")
    public ResponseEntity<?> hostMeetings(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "5") @RequestParam(defaultValue = "5") int size) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(meetingService.getHostMeetingList(user.getId(), page, size));
    }

    @Operation(summary = "참여한 모임 목록", description = "승인된(approved) 모임 목록을 조회합니다. JWT 인증 필요. /upcoming /finished 구분 추가.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/approved")
    public ResponseEntity<?> approved(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        ApprovedMeetingsResponse response = participantService.getApprovedMeetingsWithStatus(user.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "신청 대기 모임 목록", description = "PENDING 상태의 모임 목록을 조회합니다. JWT 인증 필요")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/pending")
    public ResponseEntity<?> pending(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        List<MeetingDetailResponse> items = participantService.getPendingMeetings(user.getId());
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "내 모임/참여 카운트 요약", description = "내가 만든 모임 수, 승인된 모임 수, 신청 대기 모임 수를 한 번에 반환합니다. JWT 인증 필요")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/summary")
    public ResponseEntity<?> summary(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        MyParticipationSummary summary = participantService.getMyParticipationSummary(user.getId());
        return ResponseEntity.ok(summary);
    }
}
