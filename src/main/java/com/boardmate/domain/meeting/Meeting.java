package com.boardmate.domain.meeting;

import com.boardmate.domain.game.Game;
import com.boardmate.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "meetings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meeting {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_id")
    private Long id;

    // 예: 호스트
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    // 선택: 게임
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    // 제목
    @Column(nullable = false, length = 120)
    private String title;

    // 내용
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 지역 코드
    private String regionCode;

    // 모임 장소
    private String meetingPlace;

    // 지도 API용 좌표 (피그마 지도 확대/내 위치 용)
    private Double latitude;   // 위도
    private Double longitude;  // 경도

    // 모임 일시
    private LocalDateTime meetingAt;

    // 모집 인원(최대 인원)
    private Integer maxParticipants;

    //예상 회비
    private Integer feeEstimate;

    // 태그들 JSON으로 저장
    @Column(columnDefinition = "TEXT")
    private String tagsJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GenderRestriction genderRestriction = GenderRestriction.ALL;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MeetingStatus status = MeetingStatus.OPEN;

    @Column(nullable = false)
    private Integer likeCount;

    @Column(nullable = false)
    private Integer commentCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;



    // 상태 변경 전용 메서드
    public void changeStatus(MeetingStatus newStatus) {
        this.status = newStatus;
    }

    @PrePersist
    public void prePersist() {
        if (likeCount == null) likeCount = 0;
        if (commentCount == null) commentCount = 0;
        if (status == null) status = MeetingStatus.OPEN;
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
