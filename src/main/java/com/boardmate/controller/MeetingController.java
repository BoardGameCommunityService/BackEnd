package com.boardmate.controller;

import com.boardmate.domain.meeting.Meeting;
import com.boardmate.domain.user.User;
import com.boardmate.dto.meeting.CreateMeetingRequest;
import com.boardmate.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping
    public ResponseEntity<?> create(
            @AuthenticationPrincipal User user,
            @RequestBody CreateMeetingRequest req
    ) {
        Long id = meetingService.createMeeting(user.getId(), req);
        return ResponseEntity.ok(Map.of("meetingId", id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        return ResponseEntity.ok(meetingService.getDetail(id));
    }
}

