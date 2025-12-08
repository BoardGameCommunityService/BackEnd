package com.boardmate.repository;

import com.boardmate.domain.meeting.MeetingParticipant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeetingParticipantRepository extends JpaRepository<MeetingParticipant, Long> {

    // meeting.id와 user.id를 기준으로 찾기
    Optional<MeetingParticipant> findByMeeting_IdAndUser_Id(Long meetingId, Long userId);

    // 특정 모임에서 APPROVED 상태 인원 수 카운트
    int countByMeeting_IdAndStatus(Long meetingId, String status);

    // 중복 신청 방지용
    boolean existsByMeeting_IdAndUser_Id(Long meetingId, Long userId);

    // 마이페이지 - 내가 참여한 / 대기중인 모임 리스트
    Page<MeetingParticipant> findByUser_IdAndStatus(Long userId, String status, Pageable pageable);

    List<MeetingParticipant> findByMeeting_Id(Long meetingId);


}
