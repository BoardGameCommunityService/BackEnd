package com.boardmate.repository;

import com.boardmate.domain.meeting.Meeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    @Query("SELECT m FROM Meeting m WHERE " +
            "LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.tagsJson) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Meeting> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
