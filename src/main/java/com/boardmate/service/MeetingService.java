package com.boardmate.service;

import com.boardmate.domain.game.Game;
import com.boardmate.domain.meeting.Meeting;
import com.boardmate.domain.user.User;
import com.boardmate.dto.meeting.CreateMeetingRequest;
import com.boardmate.dto.meeting.HostSummary;
import com.boardmate.dto.meeting.MeetingDetailResponse;
import com.boardmate.repository.GameRepository;
import com.boardmate.repository.MeetingParticipantRepository;
import com.boardmate.repository.MeetingRepository;
import com.boardmate.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final MeetingParticipantRepository participantRepository;

    @Transactional(readOnly = true)
    public List<MeetingDetailResponse> getMeetingList() {
        return meetingRepository.findAll().stream()
                .map(meeting -> getDetail(meeting.getId()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MeetingDetailResponse> searchMeetings(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getMeetingList();
        }
        return meetingRepository.findByKeyword(keyword)
                .stream()
                .map(meeting -> getDetail(meeting.getId()))
                .toList();
    }

    @Transactional
    public Long createMeeting(Long hostId, CreateMeetingRequest req) {

        User host = userRepository.findById(hostId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Game game = null;
        if (req.getGameId() != null) {
            game = gameRepository.findById(req.getGameId())
                    .orElse(null);
        }

        String tagsJson = convertToJson(req.getTags());

        Meeting meeting = Meeting.builder()
                .host(host)
                .game(game)
                .title(req.getTitle())
                .content(req.getContent())
                .ruleLevel(req.getRuleLevel())
                .regionCode(req.getRegionCode())
                .meetingPlace(req.getMeetingPlace())
                .meetingAt(req.getMeetingAt())
                .maxParticipants(req.getMaxParticipants())
                .feeEstimate(req.getFeeEstimate())
                .tagsJson(tagsJson)
                .status("OPEN")
                .likeCount(0)
                .commentCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        meetingRepository.save(meeting);

        return meeting.getId();
    }

    private String convertToJson(List<String> tags) {
        if (tags == null)
            return "[]";
        try {
            return new ObjectMapper().writeValueAsString(tags);
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
                .ruleLevel(meeting.getRuleLevel())
                .regionCode(meeting.getRegionCode())
                .meetingPlace(meeting.getMeetingPlace())
                .meetingAt(meeting.getMeetingAt())
                .maxParticipants(meeting.getMaxParticipants())
                .currentParticipants(current)
                .feeEstimate(meeting.getFeeEstimate())
                .status(meeting.getStatus())
                .tagsJson(meeting.getTagsJson())
                .host(hostSummary)
                .build();
    }
}
