package com.boardmate.service;

import com.boardmate.domain.game.Game;
import com.boardmate.domain.meeting.GenderRestriction;
import com.boardmate.domain.meeting.Meeting;
import com.boardmate.domain.meeting.MeetingSortType;
import com.boardmate.domain.meeting.MeetingStatus;
import com.boardmate.domain.meeting.MeetingParticipant;
import com.boardmate.domain.user.User;
import com.boardmate.dto.meeting.*;
import com.boardmate.repository.GameRepository;
import com.boardmate.repository.MeetingParticipantRepository;
import com.boardmate.repository.MeetingRepository;
import com.boardmate.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final MeetingParticipantRepository participantRepository;
    private final ObjectMapper objectMapper;

    /**
     * 모임 생성
     */
    @Transactional
    public Long createMeeting(Long hostId, CreateMeetingRequest req) {

        User host = userRepository.findById(hostId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Game game = null;
        if (req.getGameId() != null) {
            game = gameRepository.findById(req.getGameId()).orElse(null);
        }

        String tagsJson = convertToJson(req.getTags());

        Meeting meeting = Meeting.builder()
                .host(host)
                .game(game)
                .title(req.getTitle())
                .content(req.getContent())
                .regionCode(req.getRegionCode())
                .meetingPlace(req.getMeetingPlace())
                .meetingAt(req.getMeetingAt())
                .maxParticipants(req.getMaxParticipants())
                .feeEstimate(req.getFeeEstimate())
                .tagsJson(tagsJson)
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .genderRestriction(
                        req.getGenderRestriction() != null
                                ? req.getGenderRestriction()
                                : GenderRestriction.ALL
                )
                .status(MeetingStatus.OPEN)
                .likeCount(0)
                .commentCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        meetingRepository.save(meeting);
        return meeting.getId();
    }

    private String convertToJson(List<String> tags) {
        if (tags == null) return "[]";
        try {
            return objectMapper.writeValueAsString(tags);
        } catch (Exception e) {
            return "[]";
        }
    }

    private List<String> parseTags(String tagsJson) {
        if (tagsJson == null || tagsJson.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(tagsJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * 모임 상세 조회 (로그인 정보 없음 버전)
     */
    @Transactional(readOnly = true)
    public MeetingDetailResponse getDetail(Long meetingId) {
        return getDetail(meetingId, null);
    }

    /**
     * 모임 상세 조회 (내 신청 상태까지 포함)
     */
    // 로그인 유저 포함 상세 조회
    @Transactional(readOnly = true)
    public MeetingDetailResponse getDetail(Long meetingId, Long currentUserId) {

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));

        // 1) 승인된 참가자 수
        int currentApproved = participantRepository
                .countByMeeting_IdAndStatus(meetingId, "APPROVED");

        // 2) 내 신청/참여 상태 (NONE / REQUESTED / APPROVED / REJECTED)
        String myStatus = "NONE";
        if (currentUserId != null) {
            var myParticipation = participantRepository
                    .findByMeeting_IdAndUser_Id(meetingId, currentUserId);
            if (myParticipation.isPresent()) {
                myStatus = myParticipation.get().getStatus();
            }
        }

        // 3) 참가자 닉네임 목록 (승인된 사람만)
        List<String> participantNicknames = participantRepository.findByMeeting_Id(meetingId).stream()
                .filter(p -> "APPROVED".equals(p.getStatus()))
                .map(p -> p.getUser().getNickname())
                .toList();

        // 4) 지역/게임/태그 정보
        String regionText = meeting.getRegionCode();   // 나중에 사람이 읽는 한글로 매핑해도 됨

        String gameName = null;
        if (meeting.getGame() != null) {
            Game g = meeting.getGame();
            gameName = (g.getNameKo() != null) ? g.getNameKo() : g.getNameEn();
        }

        List<String> tags = parseTags(meeting.getTagsJson());

        // 5) DTO 조립 (네가 올린 of() 그대로 사용)
        return MeetingDetailResponse.of(
                meeting,
                currentApproved,
                regionText,
                gameName,
                tags,
                myStatus,
                participantNicknames
        );
    }



    /**
     * 모임 수정
     */
    @Transactional
    public Long updateMeeting(Long meetingId, Long currentUserId, UpdateMeetingRequest req) {

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));

        if (!meeting.getHost().getId().equals(currentUserId)) {
            throw new IllegalStateException("모임은 호스트만 수정할 수 있습니다.");
        }

        Game game = null;
        if (req.getGameId() != null) {
            game = gameRepository.findById(req.getGameId()).orElse(null);
        }

        String tagsJson = convertToJson(req.getTags());

        meeting.setGame(game);
        meeting.setTitle(req.getTitle());
        meeting.setContent(req.getContent());
        meeting.setRegionCode(req.getRegionCode());
        meeting.setMeetingPlace(req.getMeetingPlace());
        meeting.setMeetingAt(req.getMeetingAt());
        meeting.setMaxParticipants(req.getMaxParticipants());
        meeting.setFeeEstimate(req.getFeeEstimate());
        meeting.setTagsJson(tagsJson);
        meeting.setLatitude(req.getLatitude());
        meeting.setLongitude(req.getLongitude());
        if (req.getGenderRestriction() != null) {
            meeting.setGenderRestriction(req.getGenderRestriction());
        }
        meeting.setUpdatedAt(LocalDateTime.now());

        return meeting.getId();
    }

    /**
     * 모임 삭제 (실제 삭제 대신 상태 변경 + 참가자 있으면 막기)
     */
    @Transactional
    public void deleteMeeting(Long meetingId, Long currentUserId) {

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));

        // 권한 체크: 호스트만
        if (!meeting.getHost().getId().equals(currentUserId)) {
            throw new IllegalStateException("모임은 호스트만 삭제할 수 있습니다.");
        }

        // 참가자 있는지 확인
        int approvedCount = participantRepository
                .countByMeeting_IdAndStatus(meetingId, "APPROVED");
        if (approvedCount > 0) {
            throw new IllegalStateException("참가자가 있는 모임은 삭제할 수 없습니다.");
        }

        // 여기서는 소프트 삭제(상태 변경)만 수행
        meeting.changeStatus(MeetingStatus.CANCELLED);
        meeting.setDeletedAt(LocalDateTime.now());
        meeting.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 모임 상태 변경
     */
    @Transactional
    public void updateStatus(Long meetingId, Long currentUserId, MeetingStatus newStatus) {

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new RuntimeException("Meeting not found"));

        if (!meeting.getHost().getId().equals(currentUserId)) {
            throw new IllegalStateException("모임 상태는 호스트만 변경할 수 있습니다.");
        }

        if (meeting.getStatus() == MeetingStatus.CANCELLED ||
                meeting.getStatus() == MeetingStatus.DONE) {
            throw new IllegalStateException("이미 종료되었거나 취소된 모임은 상태를 변경할 수 없습니다.");
        }

        meeting.changeStatus(newStatus);
        meeting.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * 메인 페이지 / 목록 화면용 모임 리스트 조회
     */
    @Transactional(readOnly = true)
    public Page<MeetingCardResponse> getMeetingList(
            int page,
            int size,
            MeetingSortType sort,
            String regionCode,
            LocalDate date
    ) {
        Pageable pageable = PageRequest.of(page, size, toSort(sort));

        // 날짜 필터용 시간 범위 계산
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (date != null) {
            start = date.atStartOfDay();
            end = date.plusDays(1).atStartOfDay();
        }

        Page<Meeting> meetings = meetingRepository.findMeetingList(
                regionCode,
                start,
                end,
                pageable
        );

        // Meeting -> MeetingCardResponse 변환
        return meetings.map(meeting -> {
            int currentParticipants = participantRepository
                    .countByMeeting_IdAndStatus(meeting.getId(), "APPROVED");

            // regionCode -> regionText 변환 (지금은 일단 code 그대로)
            String regionText = meeting.getRegionCode();

            String gameName = null;
            if (meeting.getGame() != null) {
                Game g = meeting.getGame();
                gameName = g.getNameKo() != null ? g.getNameKo() : g.getNameEn();
            }

            List<String> tags = parseTags(meeting.getTagsJson());

            return MeetingCardResponse.from(
                    meeting,
                    currentParticipants,
                    regionText,
                    gameName,
                    tags
            );
        });
    }

    /**
     * 마이페이지 - 내가 만든 모임 리스트
     */
    @Transactional(readOnly = true)
    public Page<MeetingCardResponse> getMyCreatedMeetings(Long hostId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Meeting> meetings = meetingRepository.findByHost_Id(hostId, pageable);

        return meetings.map(meeting -> {
            int currentParticipants = participantRepository
                    .countByMeeting_IdAndStatus(meeting.getId(), "APPROVED");

            String regionText = meeting.getRegionCode();

            String gameName = null;
            if (meeting.getGame() != null) {
                Game g = meeting.getGame();
                gameName = g.getNameKo() != null ? g.getNameKo() : g.getNameEn();
            }

            List<String> tags = parseTags(meeting.getTagsJson());

            return MeetingCardResponse.from(
                    meeting,
                    currentParticipants,
                    regionText,
                    gameName,
                    tags
            );
        });
    }

    /**
     * 마이페이지 - 내가 참여한 모임 리스트 (APPROVED)
     */
    @Transactional(readOnly = true)
    public Page<MeetingCardResponse> getMyJoinedMeetings(Long userId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "joinedAt"));

        Page<MeetingParticipant> participants =
                participantRepository.findByUser_IdAndStatus(userId, "APPROVED", pageable);

        return participants.map(p -> {
            Meeting meeting = p.getMeeting();

            int currentParticipants = participantRepository
                    .countByMeeting_IdAndStatus(meeting.getId(), "APPROVED");

            String regionText = meeting.getRegionCode();

            String gameName = null;
            if (meeting.getGame() != null) {
                Game g = meeting.getGame();
                gameName = g.getNameKo() != null ? g.getNameKo() : g.getNameEn();
            }

            List<String> tags = parseTags(meeting.getTagsJson());

            return MeetingCardResponse.from(
                    meeting,
                    currentParticipants,
                    regionText,
                    gameName,
                    tags
            );
        });
    }

    /**
     * 마이페이지 - 내가 신청했지만 대기 중인 모임 리스트 (REQUESTED)
     */
    @Transactional(readOnly = true)
    public Page<MeetingCardResponse> getMyPendingMeetings(Long userId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "joinedAt"));

        Page<MeetingParticipant> participants =
                participantRepository.findByUser_IdAndStatus(userId, "REQUESTED", pageable);

        return participants.map(p -> {
            Meeting meeting = p.getMeeting();

            int currentParticipants = participantRepository
                    .countByMeeting_IdAndStatus(meeting.getId(), "APPROVED");

            String regionText = meeting.getRegionCode();

            String gameName = null;
            if (meeting.getGame() != null) {
                Game g = meeting.getGame();
                gameName = g.getNameKo() != null ? g.getNameKo() : g.getNameEn();
            }

            List<String> tags = parseTags(meeting.getTagsJson());

            return MeetingCardResponse.from(
                    meeting,
                    currentParticipants,
                    regionText,
                    gameName,
                    tags
            );
        });
    }

    private Sort toSort(MeetingSortType sortType) {
        return switch (sortType) {
            case LATEST -> Sort.by(Sort.Direction.DESC, "createdAt");
            case POPULAR -> Sort.by(Sort.Direction.DESC, "likeCount");
            case NEARBY -> Sort.by(Sort.Direction.ASC, "regionCode");
        };
    }

    @Transactional(readOnly = true)
    public Page<MeetingCardResponse> search(MeetingSearchRequest req) {

        // 1) 페이징 + 정렬
        Pageable pageable = PageRequest.of(
                req.getPage(),
                req.getSize(),
                toSort(req.getSort())   // 기존에 쓰던 정렬 메서드
        );

        // 2) 날짜 필터 (선택)
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (req.getDate() != null) {
            start = req.getDate().atStartOfDay();
            end = req.getDate().plusDays(1).atStartOfDay();
        }

        // 3) 1차 검색: keyword / region / game / date -> DB에서 필터
        Page<Meeting> basePage = meetingRepository.searchMeetings(
                req.getKeyword(),
                req.getRegionCode(),
                req.getGameId(),
                start,
                end,
                pageable
        );

        // 4) 2차 검색: 태그 필터 (tagsJson 안에서 in-memory 필터링)
        List<String> filterTags = req.getTags();

        Page<Meeting> filteredPage;

        if (filterTags != null && !filterTags.isEmpty()) {
            // Page<Meeting> 을 stream으로 돌려서 태그 검사
            List<Meeting> filteredContent = basePage.stream()
                    .filter(m -> {
                        String tagsJson = m.getTagsJson();
                        if (tagsJson == null || tagsJson.isBlank()) return false;

                        List<String> tags = parseTags(tagsJson);  // 이미 MeetingService 안에 있는 메서드
                        // filterTags 중 하나라도 포함되면 통과
                        return tags.stream().anyMatch(filterTags::contains);
                    })
                    .toList();

            filteredPage = new PageImpl<>(
                    filteredContent,
                    pageable,
                    filteredContent.size()
            );
        } else {
            filteredPage = basePage;
        }

        // 5) Meeting -> MeetingCardResponse 변환 (기존 리스트 조회와 동일한 규칙)
        return filteredPage.map(meeting -> {
            int currentParticipants = participantRepository
                    .countByMeeting_IdAndStatus(meeting.getId(), "APPROVED");

            String regionText = meeting.getRegionCode(); // 나중에 한글로 매핑해도 OK

            String gameName = null;
            if (meeting.getGame() != null) {
                Game g = meeting.getGame();
                gameName = (g.getNameKo() != null) ? g.getNameKo() : g.getNameEn();
            }

            List<String> tags = parseTags(meeting.getTagsJson());

            return MeetingCardResponse.from(
                    meeting,
                    currentParticipants,
                    regionText,
                    gameName,
                    tags
            );
        });
    }


    private MeetingCardResponse toMeetingCardResponse(Meeting meeting) {
        int current = participantRepository
                .countByMeeting_IdAndStatus(meeting.getId(), "APPROVED");

        String regionText = meeting.getRegionCode();

        String gameName = null;
        if (meeting.getGame() != null) {
            Game g = meeting.getGame();
            gameName = g.getNameKo() != null ? g.getNameKo() : g.getNameEn();
        }

        List<String> tags = parseTags(meeting.getTagsJson());

        return MeetingCardResponse.from(
                meeting,
                current,
                regionText,
                gameName,
                tags
        );
    }

}
