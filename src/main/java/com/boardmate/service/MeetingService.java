package com.boardmate.service;

import com.boardmate.domain.meeting.Meeting;
import com.boardmate.domain.user.User;
import com.boardmate.dto.meeting.CreateMeetingRequest;
import com.boardmate.dto.meeting.HostSummary;
import com.boardmate.dto.meeting.MeetingDetailResponse;
import com.boardmate.repository.MeetingParticipantRepository;
import com.boardmate.repository.MeetingRepository;
import com.boardmate.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final MeetingParticipantRepository participantRepository;

    @Transactional(readOnly = true)
    public Page<MeetingDetailResponse> getMeetingList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return meetingRepository.findAll(pageable)
                .map(meeting -> getDetail(meeting.getId()));
    }

    @Transactional(readOnly = true)
    public Page<MeetingDetailResponse> searchMeetings(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (keyword == null || keyword.isBlank()) {
            return getMeetingList(page, size);
        }
        return meetingRepository.findByKeyword(keyword, pageable)
                .map(meeting -> getDetail(meeting.getId()));
    }

    @Transactional
    public Long createMeeting(Long hostId, CreateMeetingRequest req) {

        User host = userRepository.findById(hostId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String gameNamesJson = convertToJson(req.getGameNames());

        Meeting meeting = Meeting.builder()
                .host(host)
                .title(req.getTitle())
                .content(req.getContent())
                .gameNamesJson(gameNamesJson)
                .meetingPlace(req.getMeetingPlace())
                .meetingAt(req.getMeetingAt())
                .maxParticipants(req.getMaxParticipants())
                .status("OPEN")
                .likeCount(0)
                .commentCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        meetingRepository.save(meeting);

        return meeting.getId();
    }

    private String convertToJson(List<?> values) {
        if (values == null)
            return "[]";
        try {
            return new ObjectMapper().writeValueAsString(values);
        } catch (Exception e) {
            return "[]";
        }
    }

    @Transactional(readOnly = true)
    public MeetingDetailResponse getDetail(Long meetingId) {

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));

        int current = participantRepository.countByMeetingIdAndStatus(
                meetingId, "APPROVED");

        HostSummary hostSummary = HostSummary.builder()
                .userId(meeting.getHost().getId())
                .nickname(meeting.getHost().getNickname())
                .avatarImageUrl(meeting.getHost().getProfileImageUrl())
                .build();

        return MeetingDetailResponse.builder()
                .meetingId(meeting.getId())
                .title(meeting.getTitle())
                .content(meeting.getContent())
                .meetingPlace(meeting.getMeetingPlace())
                .meetingAt(meeting.getMeetingAt())
                .maxParticipants(meeting.getMaxParticipants())
                .currentParticipants(current)
                .status(meeting.getStatus())
                .gameNamesJson(meeting.getGameNamesJson())
                .host(hostSummary)
                .build();
    }
}
