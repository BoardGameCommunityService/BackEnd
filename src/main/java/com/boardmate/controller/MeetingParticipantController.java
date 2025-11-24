package com.boardmate.controller;

import com.boardmate.domain.user.User;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "모집 참가", description = "보드게임 모집 참가 신청 관련 API")
@RestController
@RequestMapping("/api/meetings/{meetingId}/participants")
@RequiredArgsConstructor
public class MeetingParticipantController {

    private final MeetingParticipantService participantService;

    @Operation(
        summary = "모집 참가 신청",
        description = "특정 보드게임 모집에 참가 신청을 합니다. JWT 인증이 필요합니다."
    )
    @ApiResponse(responseCode = "200", description = "참가 신청 성공")
    @PostMapping
    public ResponseEntity<?> apply(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "모집 ID", example = "1") @PathVariable Long meetingId
    ) {
        participantService.apply(meetingId, user.getId());
        return ResponseEntity.ok(Map.of("message", "신청 완료"));
    }
}
