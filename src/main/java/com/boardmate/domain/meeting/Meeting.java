package com.boardmate.domain.meeting;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_id")
    private Long id;

    // 모임장
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    // 선택: 게임 이름 배열을 JSON 문자열로 저장
    @Column(name = "game_ids_json", columnDefinition = "TEXT")
    private String gameNamesJson;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private String meetingPlace;
    private String meetingAddress;

    private LocalDateTime meetingAt;

    private Integer maxParticipants;

    @Column(nullable = false)
    private String status; // OPEN | CLOSED

    @Column(nullable = false)
    private Integer likeCount;

    @Column(nullable = false)
    private Integer commentCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
