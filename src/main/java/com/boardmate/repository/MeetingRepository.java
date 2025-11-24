package com.boardmate.repository;

import com.boardmate.domain.meeting.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    List<Meeting> findAllByStatusOrderByCreatedAtDesc(String status);
}
