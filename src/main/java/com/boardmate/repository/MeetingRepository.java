package com.boardmate.repository;

import com.boardmate.domain.meeting.Meeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    @Query("SELECT m FROM Meeting m WHERE " +
            "LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.meetingPlace) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Meeting> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<Meeting> findByHostId(Long hostId, Pageable pageable);

    long countByHostId(Long hostId);

    Page<Meeting> findByMeetingAtBetween(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable);

    Page<Meeting> findByRegionCode(String regionCode, Pageable pageable);

    Page<Meeting> findByRegionCodeAndMeetingAtBetween(String regionCode, LocalDateTime startOfDay,
            LocalDateTime endOfDay, Pageable pageable);
}
