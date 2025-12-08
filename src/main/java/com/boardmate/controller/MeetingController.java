package com.boardmate.controller;

import com.boardmate.domain.meeting.MeetingSortType;
import com.boardmate.domain.meeting.MeetingStatus;
import com.boardmate.domain.user.User;
import com.boardmate.dto.meeting.*;
import com.boardmate.service.MeetingParticipantService;
import com.boardmate.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 모임(모집글) 관련 컨트롤러
 * - 목록 조회
 * - 상세 조회
 * - 생성 / 수정 / 삭제
 * - 모임 상태 변경
 * - 참가 신청 / 취소
 * - 참가 승인 / 거절
 */
@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;
    private final MeetingParticipantService participantService;

    // ================================
    // 1. 모집글 목록 조회 (메인 / 리스트 화면용)
    // ================================
    /**
     * 모집글 목록 조회
     * 예:
     *   GET /api/meetings?page=0&size=12&sort=LATEST&regionCode=SEOUL
     */
    @GetMapping
    public ResponseEntity<Page<MeetingCardResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "LATEST") MeetingSortType sort,
            @RequestParam(required = false) String regionCode,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date // 🔹 추가
    ) {
        Page<MeetingCardResponse> result =
                meetingService.getMeetingList(page, size, sort, regionCode, date);
        return ResponseEntity.ok(result);
    }


    // =========================
    // 2. 모집글 상세 조회
    // =========================
    /**
     * 모집글 상세 조회
     * 예: GET /api/meetings/1
     */
    @GetMapping("/{meetingId}")
    public ResponseEntity<MeetingDetailResponse> detail(
            @PathVariable Long meetingId,
            @AuthenticationPrincipal User user   // 로그인 안 했으면 null 들어올 수 있음
    ) {
        // 필요하다면 userId를 넘겨서 "내가 참가 중인지", "내 신청 상태" 같은 정보도 넣을 수 있음
        Long userId = (user != null) ? user.getId() : null;

        MeetingDetailResponse response = meetingService.getDetail(meetingId, userId);
        return ResponseEntity.ok(response);
    }

    // =========================
    // 3. 모집글 생성
    // =========================
    /**
     * 모집글 생성
     * 예: POST /api/meetings
     * body: CreateMeetingRequest
     */
    @PostMapping
    public ResponseEntity<Long> create(
            @RequestBody CreateMeetingRequest request,
            @AuthenticationPrincipal User host
    ) {
        // 서비스 시그니처는 네 코드에 맞게:
        // 예) Long id = meetingService.createMeeting(request, host.getId());
        Long meetingId = meetingService.createMeeting(host.getId(), request);

        return ResponseEntity.ok(meetingId);
    }

    // =========================
    // 4. 모집글 수정
    // =========================
    /**
     * 모집글 수정 (호스트만 가능)
     * 예: PUT /api/meetings/1
     */
    @PutMapping("/{meetingId}")
    public ResponseEntity<Void> update(
            @PathVariable Long meetingId,
            @RequestBody UpdateMeetingRequest request,
            @AuthenticationPrincipal User host
    ) {
        meetingService.updateMeeting(meetingId, host.getId(), request);
        return ResponseEntity.noContent().build();
    }

    // =========================
    // 5. 모집글 삭제
    // =========================
    /**
     * 모집글 삭제 (호스트만 가능)
     * 예: DELETE /api/meetings/1
     */
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long meetingId,
            @AuthenticationPrincipal User host
    ) {
        meetingService.deleteMeeting(meetingId, host.getId());
        return ResponseEntity.noContent().build();
    }

    // =========================
    // 6. 모집글 상태 변경 (OPEN / IN_PROGRESS / DONE / CANCELLED 등)
    // =========================
    /**
     * 모집글 상태 변경 (호스트만 가능)
     * 예: PATCH /api/meetings/1/status
     * body: { "status": "OPEN" }
     */
    @PatchMapping("/{meetingId}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long meetingId,
            @RequestBody UpdateMeetingStatusRequest request,
            @AuthenticationPrincipal User host
    ) {
        MeetingStatus newStatus = request.getStatus();
        meetingService.updateStatus(meetingId, host.getId(), newStatus);
        return ResponseEntity.noContent().build();
    }

    // =========================
    // 7. 모임 참가 신청 / 취소 (일반 유저)
    // =========================
    /**
     * 모임 참가 신청 (REQUESTED로 저장)
     * 예: POST /api/meetings/1/participants
     */
    @PostMapping("/{meetingId}/participants")
    public ResponseEntity<Void> join(
            @PathVariable Long meetingId,
            @AuthenticationPrincipal User user
    ) {
        participantService.join(meetingId, user.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * 모임 참가 취소 (신청/승인 상관 없이 취소)
     * 예: DELETE /api/meetings/1/participants
     */
    @DeleteMapping("/{meetingId}/participants")
    public ResponseEntity<Void> cancel(
            @PathVariable Long meetingId,
            @AuthenticationPrincipal User user
    ) {
        participantService.cancel(meetingId, user.getId());
        return ResponseEntity.noContent().build();
    }

    // =========================
    // 8. 참가 승인 / 거절 (호스트 전용)
    // =========================
    /**
     * 참가 신청 승인 / 거절
     * 예:
     *   PATCH /api/meetings/1/participants/5
     *   body: { "status": "APPROVED" } 또는 { "status": "REJECTED" }
     */
    @PatchMapping("/{meetingId}/participants/{userId}")
    public ResponseEntity<Void> updateParticipantStatus(
            @PathVariable Long meetingId,
            @PathVariable Long userId,
            @RequestBody UpdateParticipantStatusRequest request,
            @AuthenticationPrincipal User host
    ) {
        participantService.updateParticipantStatus(
                meetingId,
                userId,
                host.getId(),
                request.getStatus()
        );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<MeetingCardResponse>> search(MeetingSearchRequest req) {
        return ResponseEntity.ok(meetingService.search(req));
    }
}
