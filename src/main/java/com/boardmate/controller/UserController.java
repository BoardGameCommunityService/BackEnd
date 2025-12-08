package com.boardmate.controller;

import com.boardmate.domain.user.User;
import com.boardmate.dto.meeting.MeetingCardResponse;
import com.boardmate.dto.user.*;
import com.boardmate.repository.UserRepository;
import com.boardmate.service.MeetingService;
import com.boardmate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final MeetingService meetingService;

    /**
     * 내 프로필 조회
     * GET /api/users/me/profile
     */
    @GetMapping("/me/profile")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @AuthenticationPrincipal User currentUser
    ) {
        UserProfileResponse profile = userService.getMyProfile(currentUser.getId());
        return ResponseEntity.ok(profile);
    }

    /**
     * 내 프로필 수정
     * PUT /api/users/me/profile
     */
    @PutMapping("/me/profile")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @AuthenticationPrincipal User currentUser,
            @RequestBody UpdateProfileRequest request
    ) {
        UserProfileResponse updated =
                userService.updateMyProfile(currentUser.getId(), request);
        return ResponseEntity.ok(updated);
    }

    /**
     * 내가 만든 모임 리스트
     * GET /api/users/me/meetings/created?page=0&size=12
     */
    @GetMapping("/me/meetings/created")
    public ResponseEntity<Page<MeetingCardResponse>> getCreatedMeetings(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Page<MeetingCardResponse> result =
                meetingService.getMyCreatedMeetings(currentUser.getId(), page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 내가 참여한 모임 리스트 (APPROVED)
     * GET /api/users/me/meetings/joined?page=0&size=12
     */
    @GetMapping("/me/meetings/joined")
    public ResponseEntity<Page<MeetingCardResponse>> getJoinedMeetings(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Page<MeetingCardResponse> result =
                meetingService.getMyJoinedMeetings(currentUser.getId(), page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 내가 신청했지만 대기중인 모임 리스트 (REQUESTED)
     * GET /api/users/me/meetings/pending?page=0&size=12
     */
    @GetMapping("/me/meetings/pending")
    public ResponseEntity<Page<MeetingCardResponse>> getPendingMeetings(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Page<MeetingCardResponse> result =
                meetingService.getMyPendingMeetings(currentUser.getId(), page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 닉네임 중복 확인 API
     * GET /api/users/check-nickname?nickname=규민
     */
    @GetMapping("/check-nickname")
    public ResponseEntity<NicknameCheckResponse> checkNickname(
            @RequestParam String nickname
    ) {
        boolean exists = userRepository.existsByNickname(nickname);
        return ResponseEntity.ok(new NicknameCheckResponse(!exists));
    }

    @GetMapping("/me/settings/notifications")
    public ResponseEntity<NotificationSettingsResponse> getNotificationSettings(
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                userService.getNotificationSettings(currentUser.getId())
        );
    }

    @PutMapping("/me/settings/notifications")
    public ResponseEntity<Void> updateNotificationSettings(
            @AuthenticationPrincipal User currentUser,
            @RequestBody UpdateNotificationSettingsRequest req
    ) {
        userService.updateNotificationSettings(currentUser.getId(), req);
        return ResponseEntity.noContent().build();
    }

}
