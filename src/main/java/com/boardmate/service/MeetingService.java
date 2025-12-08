package com.boardmate.service;

import com.boardmate.domain.meeting.Meeting;
import com.boardmate.domain.user.User;
import com.boardmate.dto.meeting.CreateMeetingRequest;
import com.boardmate.dto.meeting.HostSummary;
import com.boardmate.dto.meeting.MeetingDetailResponse;
import com.boardmate.dto.meeting.ParticipantSummary;
import com.boardmate.dto.meeting.PopularItem;
import com.boardmate.repository.MeetingParticipantRepository;
import com.boardmate.repository.MeetingRepository;
import com.boardmate.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public Page<MeetingDetailResponse> getHostMeetingList(Long hostId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return meetingRepository.findByHostId(hostId, pageable)
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
                .meetingAddress(req.getMeetingAddress())
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

        int currentCount = participantRepository.countByMeetingId(meetingId);

        List<ParticipantSummary> participants = participantRepository.findByMeetingId(meetingId)
                .stream()
                .map(p -> ParticipantSummary.builder()
                        .userId(p.getUser().getId())
                        .nickname(p.getUser().getNickname())
                        .avatarImageUrl(p.getUser().getProfileImageUrl())
                        .build())
                .collect(Collectors.toList());

        // include host in list and count
        participants.add(0, ParticipantSummary.builder()
                .userId(meeting.getHost().getId())
                .nickname(meeting.getHost().getNickname())
                .avatarImageUrl(meeting.getHost().getProfileImageUrl())
                .build());

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
                .meetingAddress(meeting.getMeetingAddress())
                .meetingAt(meeting.getMeetingAt())
                .maxParticipants(meeting.getMaxParticipants())
                .currentParticipants(currentCount + 1)
                .status(meeting.getStatus())
                .gameNamesJson(meeting.getGameNamesJson())
                .participants(participants)
                .host(hostSummary)
                .build();
    }

    @Transactional(readOnly = true)
    public List<PopularItem> getPopularGames(int limit) {
        List<Meeting> meetings = meetingRepository.findAll();
        Map<String, Long> counter = new HashMap<>();

        for (Meeting meeting : meetings) {
            for (String game : parseGameNames(meeting.getGameNamesJson())) {
                String key = game == null ? "" : game.trim();
                if (key.isEmpty()) {
                    continue;
                }
                counter.put(key, counter.getOrDefault(key, 0L) + 1);
            }
        }

        return counter.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .map(e -> PopularItem.builder().name(e.getKey()).count(e.getValue()).build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PopularItem> getPopularRegions(int limit) {
        List<Meeting> meetings = meetingRepository.findAll();
        Map<String, Long> counter = new HashMap<>();

        for (Meeting meeting : meetings) {
            String place = meeting.getMeetingPlace();
            String key = place == null ? "" : place.trim();
            if (key.isEmpty()) {
                continue;
            }
            counter.put(key, counter.getOrDefault(key, 0L) + 1);
        }

        return counter.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .map(e -> PopularItem.builder().name(e.getKey()).count(e.getValue()).build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateMeeting(Long meetingId, Long userId, CreateMeetingRequest req) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));

        // 호스트만 수정 가능
        if (!meeting.getHost().getId().equals(userId)) {
            throw new RuntimeException("호스트만 수정할 수 있습니다.");
        }

        String gameNamesJson = convertToJson(req.getGameNames());

        meeting.setTitle(req.getTitle());
        meeting.setContent(req.getContent());
        meeting.setGameNamesJson(gameNamesJson);
        meeting.setMeetingPlace(req.getMeetingPlace());
        meeting.setMeetingAddress(req.getMeetingAddress());
        meeting.setMeetingAt(req.getMeetingAt());
        meeting.setMaxParticipants(req.getMaxParticipants());
        meeting.setUpdatedAt(LocalDateTime.now());

        meetingRepository.save(meeting);
    }

    @Transactional
    public void deleteMeeting(Long meetingId, Long userId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));

        // 호스트만 삭제 가능
        if (!meeting.getHost().getId().equals(userId)) {
            throw new RuntimeException("호스트만 삭제할 수 있습니다.");
        }

        meetingRepository.deleteById(meetingId);
    }

    private List<String> parseGameNames(String gameNamesJson) {
        if (gameNamesJson == null || gameNamesJson.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return new ObjectMapper().readValue(gameNamesJson, new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
