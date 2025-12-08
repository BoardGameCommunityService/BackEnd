package com.boardmate.domain.game;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "games")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Game {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long id;

    // 한글명 (예: 루미큐브)
    @Column(nullable = false)
    private String nameKo;
    // 영문명 (예: Rummikub)
    @Column(nullable = true)
    private String nameEn;

    // 플레이 최소 인원
    private Integer minPlayers;

    // 플레이 최대 인원
    private Integer maxPlayers;

    // 예상 플레이 시간 (분 단위)
    private Integer playTime;

    // 난이도 (1~5 또는 EASY / NORMAL / HARD 등)
    private Integer difficulty;

    // 태그 (전략, 파티 등 JSON 문자열 형태로 저장)
    @Column(columnDefinition = "TEXT")
    private String tagsJson;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
