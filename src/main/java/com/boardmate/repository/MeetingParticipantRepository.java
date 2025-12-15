package com.boardmate.repository;

import com.boardmate.domain.meeting.MeetingParticipant;
import com.boardmate.domain.meeting.MeetingParticipantId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, MeetingParticipantId> {

    Optional<MeetingParticipant> findByMeetingIdAndUserId(Long meetingId, Long userId);

    int countByMeetingId(Long meetingId);

    List<MeetingParticipant> findByMeetingId(Long meetingId);

    List<MeetingParticipant> findByUserIdAndStatus(Long userId, String status);

    long countByUserIdAndStatus(Long userId, String status);

    void deleteByUserId(Long userId);
}