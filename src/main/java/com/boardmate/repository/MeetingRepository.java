package com.boardmate.repository;

import com.boardmate.domain.meeting.Meeting;
import com.boardmate.domain.meeting.MeetingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    @Query("""
        SELECT m FROM Meeting m
        WHERE m.status = com.boardmate.domain.meeting.MeetingStatus.OPEN
          AND (:regionCode IS NULL OR m.regionCode = :regionCode)
          AND (:start IS NULL OR m.meetingAt >= :start)
          AND (:end IS NULL OR m.meetingAt < :end)
        """)
    Page<Meeting> findMeetingList(
            @Param("regionCode") String regionCode,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );

    @Query("""
    SELECT m FROM Meeting m
    WHERE m.status = com.boardmate.domain.meeting.MeetingStatus.OPEN
      AND (:keyword IS NULL OR m.title LIKE %:keyword% OR m.content LIKE %:keyword%)
      AND (:regionCode IS NULL OR m.regionCode = :regionCode)
      AND (:gameId IS NULL OR m.game.id = :gameId)
      AND (:start IS NULL OR m.meetingAt >= :start)
      AND (:end IS NULL OR m.meetingAt < :end)
    """)
    Page<Meeting> searchMeetings(
            @Param("keyword") String keyword,
            @Param("regionCode") String regionCode,
            @Param("gameId") Long gameId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );


    // 마이페이지 - 내가 만든 모임
    Page<Meeting> findByHost_Id(Long hostId, Pageable pageable);

    int countByIdAndStatus(Long meetingId, MeetingStatus status);

}
