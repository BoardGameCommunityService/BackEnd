package com.boardmate.service;

import com.boardmate.domain.notification.Notification;
import com.boardmate.domain.notification.NotificationSetting;
import com.boardmate.domain.user.User;
import com.boardmate.repository.NotificationRepository;
import com.boardmate.repository.NotificationSettingRepository;
import com.boardmate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationSettingRepository settingRepository;
    private final UserRepository userRepository;
    private final com.boardmate.repository.MeetingRepository meetingRepository;
    private final MeetingService meetingService;

    /**
     * 알림 생성
     */
    @Transactional
    public void createNotification(Long userId, String type, String title, String message, Long resourceId,
            Long relatedUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 알림 설정 확인
        NotificationSetting setting = settingRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultNotificationSetting(user));

        boolean shouldNotify = shouldSendNotification(setting, type);

        if (!shouldNotify) {
            return;
        }

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .resourceId(resourceId)
                .relatedUserId(relatedUserId)
                .build();

        notificationRepository.save(notification);
    }

    /**
     * 지역 기반 모임 알림 (활성 사용자만 대상으로 발송)
     */
    @Transactional
    public void notifyRegionMeeting(Long meetingHostId, String region, Long meetingId, String meetingTitle) {
        // 활성 사용자에게만 알림 발송 (호스트 제외)
        List<User> activeUsers = userRepository.findByIsActiveTrue();

        for (User user : activeUsers) {
            if (user.getId().equals(meetingHostId)) {
                continue; // 호스트는 제외
            }

            createNotification(
                    user.getId(),
                    "REGION_MEETING",
                    "지역 모임 개설",
                    region + " 지역에 새로운 모임 '" + meetingTitle + "'이 개설되었습니다.",
                    meetingId,
                    null);
        }
    }

    /**
     * 답변 달림 알림
     */
    @Transactional
    public void notifyAnswer(Long inquiryUserId, Long inquiryId, String inquiryTitle) {
        createNotification(
                inquiryUserId,
                "ANSWER",
                "문의한 글에 답변이 도착했어요.",
                "답변을 확인해보세요.",
                inquiryId,
                null);
    }

    /**
     * 모임 신청 알림 (호스트에게)
     */
    @Transactional
    public void notifyMeetingApplication(Long hostId, Long meetingId, String applicantName, String meetingTitle,
            Long applicantId) {
        createNotification(
                hostId,
                "MEETING_APPLICATION",
                "'" + applicantName + "'님이 모임 참가 신청을 보냈어요.",
                "새로운 보드메이트를 만나보세요.",
                meetingId,
                applicantId);
    }

    /**
     * 모임 신청 수락 알림 (신청자에게)
     */
    @Transactional
    public void notifyApplicationApproved(Long userId, Long meetingId, String meetingTitle) {
        createNotification(
                userId,
                "APPLICATION_APPROVED",
                "모임 신청이 수락되었어요.",
                "보드메이트와 함께 게임을 즐겨보세요.",
                meetingId,
                null);
    }

    /**
     * 모임 신청 반려 알림 (신청자에게)
     */
    @Transactional
    public void notifyApplicationDenied(Long userId, Long meetingId, String meetingTitle) {
        createNotification(
                userId,
                "APPLICATION_DENIED",
                "모임 신청이 반려되었어요.",
                "아쉽지만 다른 모임에 참가해보세요.",
                meetingId,
                null);
    }

    /**
     * 알림 조회 (페이징)
     */
    @Transactional
    public com.boardmate.dto.notification.NotificationsResponse getUserNotificationsMerged(Long userId, int page,
            int size) {
        // 1) 영속 알림 (전체 조회) — total이 page size에 따라 달라지는 문제 방지
        List<Notification> persistedAll = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);

        List<com.boardmate.dto.notification.NotificationItem> items = new ArrayList<>();
        for (Notification n : persistedAll) {
            items.add(com.boardmate.dto.notification.NotificationItem.builder()
                    .id(n.getId())
                    .type(n.getType())
                    .title(n.getTitle())
                    .message(n.getMessage())
                    .resourceId(n.getResourceId())
                    .relatedUserId(n.getRelatedUserId())
                    .createdAt(n.getCreatedAt())
                    .build());
        }

        // 2) 동적 지역 알림 생성 (저장하지 않음)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        NotificationSetting setting = settingRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultNotificationSetting(user));

        if (Boolean.TRUE.equals(setting.getIsEnabled()) && user.getRegion() != null && !user.getRegion().isBlank()) {
            List<com.boardmate.domain.meeting.Meeting> futureMeetings = meetingRepository
                    .findByRegionCodeContainingAndMeetingAtAfter(user.getRegion(), LocalDateTime.now());
            if (!futureMeetings.isEmpty()) {
                com.boardmate.domain.meeting.Meeting latest = futureMeetings.stream()
                        .max(Comparator.comparing(com.boardmate.domain.meeting.Meeting::getCreatedAt))
                        .orElse(futureMeetings.get(0));
                items.add(com.boardmate.dto.notification.NotificationItem.builder()
                        .id(null)
                        .type("REGION_MEETING")
                        .title(user.getRegion() + "에 새로운 모임이 개설되었어요!")
                        .message("개설된 모임을 확인해보세요.")
                        .resourceId(latest.getId())
                        .relatedUserId(null)
                        .createdAt(latest.getCreatedAt())
                        .build());
            }
        }

        // 3) 정렬 및 총합
        items = items.stream()
                .sorted(Comparator.comparing(com.boardmate.dto.notification.NotificationItem::getCreatedAt).reversed())
                .collect(Collectors.toList());

        // 4) 수동 페이징
        int from = Math.min(page * size, items.size());
        int to = Math.min(from + size, items.size());
        List<com.boardmate.dto.notification.NotificationItem> pagedItems = items.subList(from, to);

        return com.boardmate.dto.notification.NotificationsResponse.builder()
                .items(pagedItems)
                .total(items.size())
                .build();
    }

    /**
     * 알림 여부 (읽음/읽지않음 무관)
     */
    @Transactional
    public boolean hasNotifications(Long userId) {
        if (notificationRepository.countByUserId(userId) > 0)
            return true;
        User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            return false;
        NotificationSetting setting = settingRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultNotificationSetting(user));
        if (!Boolean.TRUE.equals(setting.getIsEnabled()))
            return false;
        if (user.getRegion() == null || user.getRegion().isBlank())
            return false;
        return !meetingRepository.findByRegionCodeContainingAndMeetingAtAfter(user.getRegion(), LocalDateTime.now())
                .isEmpty();
    }

    /**
     * 사용자의 소속 지역 내 활성(현재시점 이후) 모임 목록 반환
     */
    @Transactional(readOnly = true)
    public java.util.List<com.boardmate.dto.meeting.MeetingSummary> getMyRegionActiveMeetings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (user.getRegion() == null || user.getRegion().isBlank()) {
            return java.util.Collections.emptyList();
        }

        java.util.List<com.boardmate.domain.meeting.Meeting> meetings = meetingRepository
                .findByRegionCodeContainingAndMeetingAtAfter(user.getRegion(), LocalDateTime.now());

        // 최신순 정렬 (meetingAt 오름차순이 필요하면 변경 가능)
        meetings.sort(java.util.Comparator.comparing(com.boardmate.domain.meeting.Meeting::getMeetingAt));

        java.util.List<com.boardmate.dto.meeting.MeetingSummary> result = new java.util.ArrayList<>();
        for (com.boardmate.domain.meeting.Meeting m : meetings) {
            com.boardmate.dto.meeting.MeetingDetailResponse detail = meetingService.getDetail(m.getId());
            com.boardmate.dto.meeting.MeetingSummary summary = com.boardmate.dto.meeting.MeetingSummary.builder()
                    .meetingId(detail.getMeetingId())
                    .title(detail.getTitle())
                    .content(detail.getContent())
                    .meetingPlace(detail.getMeetingPlace())
                    .meetingAddress(detail.getMeetingAddress())
                    .regionCode(detail.getRegionCode())
                    .meetingAt(detail.getMeetingAt())
                    .maxParticipants(detail.getMaxParticipants())
                    .currentParticipants(detail.getCurrentParticipants())
                    .status(detail.getStatus())
                    .gameNamesJson(detail.getGameNamesJson())
                    .host(detail.getHost())
                    .build();
            result.add(summary);
        }

        return result;
    }

    /**
     * 알림 설정 조회
     */
    @Transactional
    public NotificationSetting getNotificationSetting(Long userId) {
        return settingRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultNotificationSetting(
                        userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."))));
    }

    /**
     * 알림 설정 업데이트
     */
    @Transactional
    public void updateNotificationSetting(Long userId, Boolean isEnabled) {
        NotificationSetting setting = getNotificationSetting(userId);

        if (isEnabled != null) {
            setting.setEnabled(isEnabled);
        }

        settingRepository.save(setting);
    }

    /**
     * 기본 알림 설정 생성
     */
    private NotificationSetting createDefaultNotificationSetting(User user) {
        NotificationSetting setting = NotificationSetting.builder()
                .user(user)
                .build();
        // 필드/빌더 명 충돌을 피하기 위해 생성 후 세터로 활성화 설정
        setting.setEnabled(true);
        return settingRepository.save(setting);
    }

    /**
     * 알림을 보낼지 결정 (설정이 활성화되어 있으면 true)
     */
    private boolean shouldSendNotification(NotificationSetting setting, String type) {
        return setting.getIsEnabled();
    }

    /**
     * 모임 신청 알림 삭제 (신청 취소 시)
     */
    @Transactional
    public void deleteMeetingApplicationNotification(Long hostId, Long meetingId, Long applicantUserId) {
        notificationRepository.deleteByUserIdAndTypeAndResourceIdAndRelatedUserId(
                hostId,
                "MEETING_APPLICATION",
                meetingId,
                applicantUserId);
    }
}
