package com.boardmate.repository;

import com.boardmate.domain.meeting.MeetingParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Long> {

    Optional<MeetingParticipant> findByMeetingIdAndUserId(Long meetingId, Long userId);

    int countByMeetingIdAndStatus(Long meetingId, String status);
}
