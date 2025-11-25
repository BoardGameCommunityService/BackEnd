package com.boardmate.controller;

import com.boardmate.domain.user.User;
import com.boardmate.dto.meeting.CreateMeetingRequest;
import com.boardmate.service.MeetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "모집", description = "보드게임 모집 관련 API")
@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @Operation(summary = "모집 생성", description = "새로운 보드게임 모집을 생성합니다. JWT 인증이 필요합니다.")
    @ApiResponse(responseCode = "200", description = "모집 생성 성공")
    @PostMapping
    public ResponseEntity<?> create(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @RequestBody CreateMeetingRequest req) {
        Long id = meetingService.createMeeting(user.getId(), req);
        return ResponseEntity.ok(Map.of("meetingId", id));
    }

    @Operation(summary = "모집 상세 조회", description = "모집 ID로 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(
            @Parameter(description = "모집 ID", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(meetingService.getDetail(id));
    }
}
