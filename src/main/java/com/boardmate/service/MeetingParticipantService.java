package com.boardmate.service;

import com.boardmate.domain.meeting.Meeting;
import com.boardmate.domain.meeting.MeetingParticipant;
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

    @Transactional
    public void apply(Long meetingId, Long userId) {

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));

        if (meeting.getHost().getId().equals(userId)) {
            throw new RuntimeException("호스트는 참가 신청할 수 없습니다.");
        }

        participantRepository.findByMeetingIdAndUserId(meetingId, userId)
                .ifPresent(p -> { throw new RuntimeException("이미 신청했습니다."); });

        int approvedCount =
                participantRepository.countByMeetingIdAndStatus(meetingId, "APPROVED");

        if (approvedCount >= meeting.getMaxParticipants()) {
            throw new RuntimeException("정원이 가득 찼습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow();

        MeetingParticipant participant = MeetingParticipant.builder()
                .meeting(meeting)
                .user(user)
                .status("REQUESTED")
                .joinedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        participantRepository.save(participant);
    }
}
