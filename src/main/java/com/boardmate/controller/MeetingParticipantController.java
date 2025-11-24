package com.boardmate.controller;

import com.boardmate.domain.user.User;
import com.boardmate.service.MeetingParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/meetings/{meetingId}/participants")
@RequiredArgsConstructor
public class MeetingParticipantController {

    private final MeetingParticipantService participantService;

    @PostMapping
    public ResponseEntity<?> apply(
            @AuthenticationPrincipal User user,
            @PathVariable Long meetingId
    ) {
        participantService.apply(meetingId, user.getId());
        return ResponseEntity.ok(Map.of("message", "신청 완료"));
    }
}
