package com.boardmate.controller;

import com.boardmate.domain.user.User;
import com.boardmate.dto.meeting.MeetingParticipantsResponse;
import com.boardmate.service.MeetingParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "모집참가", description = "보드게임 모집 참가 신청 관련 API")
@RestController
@RequestMapping("/api/meetings/{meetingId}/participants")
@RequiredArgsConstructor
public class MeetingParticipantController {

    private final MeetingParticipantService participantService;

    @Operation(summary = "모집 참가 신청", description = "특정 보드게임 모집에 참가 신청을 합니다. JWT 인증이 필요합니다.")
    @ApiResponse(responseCode = "200", description = "참가 신청 성공")
    @PostMapping
    public ResponseEntity<?> apply(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "모집 ID", example = "1") @PathVariable Long meetingId) {
        try {
            MeetingParticipantsResponse response = participantService.apply(meetingId, user.getId());
            Map<String, Object> result = new HashMap<>();
            result.put("data", response);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            String code = "UNKNOWN_ERROR";
            String message = e.getMessage();

            if (message != null) {
                if (message.contains("모임을 찾을 수 없습니다")) {
                    code = "MEETING_NOT_FOUND";
                } else if (message.contains("호스트는 참가 신청할 수 없습니다")) {
                    code = "HOST_CANNOT_APPLY";
                } else if (message.contains("이미 신청했습니다")) {
                    code = "ALREADY_APPLIED";
                } else if (message.contains("정원이 가득 찼습니다")) {
                    code = "MEETING_FULL";
                } else if (message.contains("사용자를 찾을 수 없습니다")) {
                    code = "USER_NOT_FOUND";
                }
            }

            Map<String, String> error = new HashMap<>();
            error.put("code", code);
            error.put("message", message);
            return ResponseEntity.status(400).body(error);
        }
    }

    @Operation(summary = "모집 참가 신청 취소", description = "신청한 모집 참가를 취소합니다. JWT 인증이 필요합니다.")
    @ApiResponse(responseCode = "200", description = "취소 성공")
    @DeleteMapping
    public ResponseEntity<?> cancel(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "모집 ID", example = "1") @PathVariable Long meetingId) {
        try {
            MeetingParticipantsResponse response = participantService.cancel(meetingId, user.getId());
            Map<String, Object> result = new HashMap<>();
            result.put("data", response);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            String code = "UNKNOWN_ERROR";
            String message = e.getMessage();

            if (message != null) {
                if (message.contains("모임을 찾을 수 없습니다")) {
                    code = "MEETING_NOT_FOUND";
                } else if (message.contains("신청 기록을 찾을 수 없습니다")) {
                    code = "PARTICIPATION_NOT_FOUND";
                }
            }

            Map<String, String> error = new HashMap<>();
            error.put("code", code);
            error.put("message", message);
            return ResponseEntity.status(400).body(error);
        }
    }

    @Operation(summary = "참가 신청 승인", description = "호스트가 참가 신청을 승인합니다. JWT 인증이 필요합니다.")
    @ApiResponse(responseCode = "200", description = "승인 성공")
    @PatchMapping("/{userId}/approve")
    public ResponseEntity<?> approve(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "모집 ID", example = "1") @PathVariable Long meetingId,
            @Parameter(description = "참가자 User ID", example = "2") @PathVariable Long userId) {
        try {
            MeetingParticipantsResponse response = participantService.approveParticipant(meetingId, userId,
                    user.getId());
            Map<String, Object> result = new HashMap<>();
            result.put("data", response);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            String code = "UNKNOWN_ERROR";
            String message = e.getMessage();

            if (message != null) {
                if (message.contains("모임을 찾을 수 없습니다")) {
                    code = "MEETING_NOT_FOUND";
                } else if (message.contains("호스트만 승인할 수 있습니다")) {
                    code = "NOT_HOST";
                } else if (message.contains("신청 기록을 찾을 수 없습니다")) {
                    code = "PARTICIPATION_NOT_FOUND";
                }
            }

            Map<String, String> error = new HashMap<>();
            error.put("code", code);
            error.put("message", message);
            return ResponseEntity.status(400).body(error);
        }
    }

    @Operation(summary = "참가 신청 거절", description = "호스트가 참가 신청을 거절합니다. JWT 인증이 필요합니다.")
    @ApiResponse(responseCode = "200", description = "거절 성공")
    @PatchMapping("/{userId}/deny")
    public ResponseEntity<?> deny(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "모집 ID", example = "1") @PathVariable Long meetingId,
            @Parameter(description = "참가자 User ID", example = "2") @PathVariable Long userId) {
        try {
            MeetingParticipantsResponse response = participantService.denyParticipant(meetingId, userId, user.getId());
            Map<String, Object> result = new HashMap<>();
            result.put("data", response);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            String code = "UNKNOWN_ERROR";
            String message = e.getMessage();

            if (message != null) {
                if (message.contains("모임을 찾을 수 없습니다")) {
                    code = "MEETING_NOT_FOUND";
                } else if (message.contains("호스트만 거절할 수 있습니다")) {
                    code = "NOT_HOST";
                } else if (message.contains("신청 기록을 찾을 수 없습니다")) {
                    code = "PARTICIPATION_NOT_FOUND";
                }
            }

            Map<String, String> error = new HashMap<>();
            error.put("code", code);
            error.put("message", message);
            return ResponseEntity.status(400).body(error);
        }
    }
}
