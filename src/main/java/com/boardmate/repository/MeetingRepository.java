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

    Page<Meeting> findByRegionCodeContaining(String regionCode, Pageable pageable);

    Page<Meeting> findByRegionCodeContainingAndMeetingAtBetween(String regionCode, LocalDateTime startOfDay,
            LocalDateTime endOfDay, Pageable pageable);

    // 동적 지역 알림용: 사용자의 지역코드와 현재시점 이후 모임 조회
    java.util.List<Meeting> findByRegionCodeContainingAndMeetingAtAfter(String regionCode, LocalDateTime now);

    void deleteByHostId(Long hostId);
}
