package com.boardmate.service;

import com.boardmate.domain.meeting.Meeting;
import com.boardmate.domain.meeting.MeetingParticipant;
import com.boardmate.domain.user.User;
import com.boardmate.dto.meeting.MeetingDetailResponse;
import com.boardmate.dto.meeting.MeetingParticipantsResponse;
import com.boardmate.dto.meeting.MyParticipationSummary;
import com.boardmate.dto.meeting.ParticipantSummary;
import com.boardmate.repository.MeetingParticipantRepository;
import com.boardmate.repository.MeetingRepository;
import com.boardmate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MeetingParticipantService {

    private final MeetingRepository meetingRepository;
    private final MeetingParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final MeetingService meetingService;

    @Transactional
    public MeetingParticipantsResponse apply(Long meetingId, Long userId) {

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("모임을 찾을 수 없습니다."));

        if (meeting.getHost().getId().equals(userId)) {
            throw new RuntimeException("호스트는 참가 신청할 수 없습니다.");
        }

        participantRepository.findByMeetingIdAndUserId(meetingId, userId)
                .ifPresent(p -> {
                    throw new RuntimeException("이미 신청했습니다.");
                });

        int currentCount = participantRepository.countByMeetingId(meetingId);

        Integer maxParticipants = meeting.getMaxParticipants();
        if (maxParticipants != null && currentCount + 1 >= maxParticipants) { // +1 for host
            throw new RuntimeException("정원이 가득 찼습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        MeetingParticipant participant = MeetingParticipant.builder()
                .meeting(meeting)
                .user(user)
                .status("PENDING")
                .joinedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        participantRepository.save(participant);

        return buildParticipantsResponse(meetingId, meeting);
    }

    @Transactional
    public MeetingParticipantsResponse cancel(Long meetingId, Long userId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("모임을 찾을 수 없습니다."));

        MeetingParticipant participant = participantRepository.findByMeetingIdAndUserId(meetingId, userId)
                .orElseThrow(() -> new RuntimeException("신청 기록을 찾을 수 없습니다."));

        participantRepository.delete(participant);

        return buildParticipantsResponse(meetingId, meeting);
    }

    private MeetingParticipantsResponse buildParticipantsResponse(Long meetingId, Meeting meeting) {
        int currentCount = participantRepository.countByMeetingId(meetingId);

        List<ParticipantSummary> participants = new ArrayList<>();

        // include host
        participants.add(ParticipantSummary.builder()
                .userId(meeting.getHost().getId())
                .nickname(meeting.getHost().getNickname())
                .avatarImageUrl(meeting.getHost().getProfileImageUrl())
                .build());

        participantRepository.findByMeetingId(meetingId)
                .forEach(p -> participants.add(ParticipantSummary.builder()
                        .userId(p.getUser().getId())
                        .nickname(p.getUser().getNickname())
                        .avatarImageUrl(p.getUser().getProfileImageUrl())
                        .status(p.getStatus())
                        .build()));

        return MeetingParticipantsResponse.builder()
                .currentParticipants(currentCount + 1) // include host
                .participants(participants)
                .build();
    }

    @Transactional
    public MeetingParticipantsResponse approveParticipant(Long meetingId, Long participantUserId, Long hostId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("모임을 찾을 수 없습니다."));

        // 호스트만 승인 가능
        if (!meeting.getHost().getId().equals(hostId)) {
            throw new RuntimeException("호스트만 승인할 수 있습니다.");
        }

        MeetingParticipant participant = participantRepository.findByMeetingIdAndUserId(meetingId, participantUserId)
                .orElseThrow(() -> new RuntimeException("신청 기록을 찾을 수 없습니다."));

        participant.setStatus("APPROVED");
        participant.setUpdatedAt(LocalDateTime.now());
        participantRepository.save(participant);

        return buildParticipantsResponse(meetingId, meeting);
    }

    @Transactional
    public MeetingParticipantsResponse denyParticipant(Long meetingId, Long participantUserId, Long hostId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("모임을 찾을 수 없습니다."));

        // 호스트만 거절 가능
        if (!meeting.getHost().getId().equals(hostId)) {
            throw new RuntimeException("호스트만 거절할 수 있습니다.");
        }

        MeetingParticipant participant = participantRepository.findByMeetingIdAndUserId(meetingId, participantUserId)
                .orElseThrow(() -> new RuntimeException("신청 기록을 찾을 수 없습니다."));

        participant.setStatus("DENIED");
        participant.setUpdatedAt(LocalDateTime.now());
        participantRepository.save(participant);

        return buildParticipantsResponse(meetingId, meeting);
    }

    @Transactional(readOnly = true)
    public List<MeetingDetailResponse> getApprovedMeetings(Long userId) {
        return getMeetingsByStatus(userId, "APPROVED");
    }

    @Transactional(readOnly = true)
    public List<MeetingDetailResponse> getPendingMeetings(Long userId) {
        return getMeetingsByStatus(userId, "PENDING");
    }

    private List<MeetingDetailResponse> getMeetingsByStatus(Long userId, String status) {
        List<MeetingParticipant> participants = participantRepository.findByUserIdAndStatus(userId, status);
        Set<Long> meetingIds = new HashSet<>();
        List<MeetingDetailResponse> result = new ArrayList<>();

        for (MeetingParticipant participant : participants) {
            Long meetingId = participant.getMeeting().getId();
            // 중복 방지
            if (meetingIds.add(meetingId)) {
                result.add(meetingService.getDetail(meetingId));
            }
        }
        return result;
    }

    @Transactional(readOnly = true)
    public MyParticipationSummary getMyParticipationSummary(Long userId) {
        long hostCount = meetingRepository.countByHostId(userId);
        long approvedCount = participantRepository.countByUserIdAndStatus(userId, "APPROVED");
        long pendingCount = participantRepository.countByUserIdAndStatus(userId, "PENDING");
        return new MyParticipationSummary(hostCount, approvedCount, pendingCount);
    }
}
