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

    @Operation(summary = "모집 목록 조회", description = "모든 보드게임 모집 목록을 조회합니다. 인증 불필요. 페이징 지원.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<?> list(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "5") @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(meetingService.getMeetingList(page, size));
    }

    @Operation(summary = "모집 검색", description = "제목, 내용, 태그로 보드게임 모집을 검색합니다. 인증 불필요. 페이징 지원.")
    @ApiResponse(responseCode = "200", description = "검색 성공")
    @GetMapping("/search")
    public ResponseEntity<?> search(
            @Parameter(description = "검색 키워드", example = "초심자") @RequestParam String keyword,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "5") @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(meetingService.searchMeetings(keyword, page, size));
    }

    @Operation(summary = "모집 상세 조회", description = "모집 ID로 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(
            @Parameter(description = "모집 ID", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(meetingService.getDetail(id));
    }
}
