package com.boardmate.controller;

import com.boardmate.domain.notification.NotificationSetting;
import com.boardmate.domain.user.User;
import com.boardmate.dto.notification.NotificationSettingRequest;
import com.boardmate.dto.meeting.MeetingSummary;
import com.boardmate.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "알림", description = "알림 조회 및 설정 관련 API")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 목록 조회", description = "사용자의 알림 목록을 조회합니다. JWT 인증 필요. 페이징 지원. 지역 기반 모임 알림은 동적으로 포함됩니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<?> getNotifications(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        var response = notificationService.getUserNotificationsMerged(user.getId(), page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 지역 활성 모임 목록", description = "본인 소속 지역 내 현재시점 이후(활성) 모임 목록을 반환합니다. JWT 인증 필요.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/region/meetings")
    public ResponseEntity<?> getMyRegionMeetings(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        java.util.List<MeetingSummary> items = notificationService.getMyRegionActiveMeetings(user.getId());
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "알림 여부", description = "알림이 있는지 확인합니다. JWT 인증 필요.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/has-unread")
    public ResponseEntity<?> hasUnread(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        boolean hasNotifications = notificationService.hasNotifications(user.getId());
        return ResponseEntity.ok(Map.of("hasNotifications", hasNotifications));
    }

    @Operation(summary = "알림 설정 조회", description = "사용자의 알림 설정을 조회합니다. JWT 인증 필요.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/settings")
    public ResponseEntity<?> getSettings(@Parameter(hidden = true) @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        NotificationSetting setting = notificationService.getNotificationSetting(user.getId());
        return ResponseEntity.ok(setting);
    }

    @Operation(summary = "알림 설정 업데이트", description = "사용자의 알림 설정을 업데이트합니다. JWT 인증 필요.")
    @ApiResponse(responseCode = "200", description = "업데이트 성공")
    @PutMapping("/settings")
    public ResponseEntity<?> updateSettings(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @RequestBody NotificationSettingRequest request) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            notificationService.updateNotificationSetting(
                    user.getId(),
                    request.getIsEnabled());
            NotificationSetting updated = notificationService.getNotificationSetting(user.getId());
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }
}
