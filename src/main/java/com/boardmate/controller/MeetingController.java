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
import java.util.List;

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

    @Operation(summary = "모집 목록 조회", description = "모든 보드게임 모집 목록을 조회합니다. 인증 불필요. 페이징 지원. 날짜로 필터링 가능 (YYYYMMDD 형식).")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<?> list(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "5") @RequestParam(defaultValue = "5") int size,
            @Parameter(description = "조회 날짜 (YYYYMMDD 형식, 선택사항, null be possible)", example = "20251210") @RequestParam(required = false) String date) {
        return ResponseEntity.ok(meetingService.getMeetingList(page, size, date));
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

    @Operation(summary = "인기 게임 목록", description = "모임 등록 기준으로 인기 게임을 집계해 내림차순으로 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/popular/games")
    public ResponseEntity<?> popularGames(
            @Parameter(description = "최대 반환 개수", example = "10") @RequestParam(defaultValue = "10") int limit) {
        List<?> items = meetingService.getPopularGames(limit);
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "인기 지역 목록", description = "모임 등록 기준으로 인기 지역을 집계해 내림차순으로 반환합니다."
            + "<br><br>"
            + "모임 생성 시 추가하는 meetingPlace 기준으로 집계합니다."
            + "<br><br>"
            + "지역별 코드 혹은 문자열이 필수로 지정되어야 합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/popular/regions")
    public ResponseEntity<?> popularRegions(
            @Parameter(description = "최대 반환 개수", example = "10") @RequestParam(defaultValue = "10") int limit) {
        List<?> items = meetingService.getPopularRegions(limit);
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "내가 만든 모임 목록", description = "호스트가 만든 모임 목록을 조회합니다. 페이징 지원.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/host/meetings")
    public ResponseEntity<?> hostMeetings(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "5") @RequestParam(defaultValue = "5") int size) {
        return ResponseEntity.ok(meetingService.getHostMeetingList(user.getId(), page, size));
    }

    @Operation(summary = "모집 상세 조회", description = "모집 ID로 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(
            @Parameter(description = "모집 ID", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(meetingService.getDetail(id));
    }

    @Operation(summary = "모집 수정", description = "생성된 모집을 수정합니다. JWT 인증이 필요하며, 호스트만 수정 가능합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "모집 ID", example = "1") @PathVariable Long id,
            @RequestBody CreateMeetingRequest req) {
        try {
            meetingService.updateMeeting(id, user.getId(), req);
            return ResponseEntity.ok(Map.of("message", "수정 완료"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of(
                    "code", "UPDATE_FAILED",
                    "message", e.getMessage()));
        }
    }

    @Operation(summary = "모집 삭제", description = "모집을 삭제합니다. JWT 인증이 필요하며, 호스트만 삭제 가능합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "모집 ID", example = "1") @PathVariable Long id) {
        try {
            meetingService.deleteMeeting(id, user.getId());
            return ResponseEntity.ok(Map.of("message", "삭제 완료"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of(
                    "code", "DELETE_FAILED",
                    "message", e.getMessage()));
        }
    }
}
