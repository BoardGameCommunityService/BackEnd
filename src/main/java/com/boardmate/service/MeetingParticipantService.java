package com.boardmate.service;

import com.boardmate.domain.meeting.Meeting;
import com.boardmate.domain.meeting.MeetingParticipant;
import com.boardmate.domain.meeting.MeetingStatus;
import com.boardmate.domain.user.User;
import com.boardmate.repository.MeetingParticipantRepository;
import com.boardmate.repository.MeetingRepository;
import com.boardmate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MeetingParticipantService {

    private final MeetingRepository meetingRepository;
    private final MeetingParticipantRepository participantRepository;
    private final UserRepository userRepository;

    /**
     * 참가 신청 (승인 대기 REQUESTED)
     */
    @Transactional
    public void join(Long meetingId, Long userId) {

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임이 존재하지 않습니다."));

        // 모임 상태 확인 — 모집 중이 아닐 경우 신청 불가
        if (meeting.getStatus() != MeetingStatus.OPEN) {
            throw new IllegalStateException("모집 중인 모임이 아닙니다.");
        }

        // 호스트 본인은 신청 불가
        if (meeting.getHost().getId().equals(userId)) {
            throw new IllegalStateException("모임장은 직접 참가 신청을 할 수 없습니다.");
        }

        // 중복 신청 방지
        if (participantRepository.existsByMeeting_IdAndUser_Id(meetingId, userId)) {
            throw new IllegalStateException("이미 이 모임에 신청했거나 참가 중입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        MeetingParticipant participant = MeetingParticipant.create(user, meeting);

        participantRepository.save(participant);
    }

    /**
     * 승인 / 거절 처리 — 호스트만 가능
     */
    @Transactional
    public void updateParticipantStatus(
            Long meetingId,
            Long targetUserId,
            Long hostUserId,
            String newStatus
    ) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모임이 존재하지 않습니다."));

        // 호스트 권한 체크
        if (!meeting.getHost().getId().equals(hostUserId)) {
            throw new IllegalStateException("모임장만 참가 신청을 승인하거나 거절할 수 있습니다.");
        }

        MeetingParticipant participant = participantRepository
                .findByMeeting_IdAndUser_Id(meetingId, targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("해당 참가 신청을 찾을 수 없습니다."));

        // 상태값 검증
        if (!"APPROVED".equals(newStatus) && !"REJECTED".equals(newStatus)) {
            throw new IllegalArgumentException("허용되지 않는 상태값입니다.");
        }

        // 승인 시 정원 체크
        if ("APPROVED".equals(newStatus)) {
            int approvedCount = participantRepository.countByMeeting_IdAndStatus(meetingId, "APPROVED");

            Integer max = meeting.getMaxParticipants();
            if (max != null && approvedCount >= max) {
                throw new IllegalStateException("모임 정원이 가득 찼습니다.");
            }
        }

        // 상태 변경
        participant.setStatus(newStatus);
    }

    /**
     * 참가 취소 (요청/승인 모두 취소 가능)
     */
    @Transactional
    public void cancel(Long meetingId, Long userId) {
        MeetingParticipant participant = participantRepository
                .findByMeeting_IdAndUser_Id(meetingId, userId)
                .orElseThrow(() -> new IllegalArgumentException("참가 신청 기록이 없습니다."));

        participantRepository.delete(participant);
    }
}
