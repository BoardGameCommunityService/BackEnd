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
    private Long id;

    // 모임장
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    // 선택: 게임
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String ruleLevel;  // ANY | BEGINNER

    private String regionCode;

    private String meetingPlace;

    private LocalDateTime meetingAt;

    private Integer maxParticipants;

    private Integer feeEstimate;

    @Column(columnDefinition = "TEXT")
    private String tagsJson;

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
