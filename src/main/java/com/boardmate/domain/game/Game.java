package com.boardmate.domain.game;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long id;

    private String nameKo;
    private String nameEn;

    private Integer minPlayers;
    private Integer maxPlayers;

    private Integer minPlaytime;
    private Integer maxPlaytime;

    private BigDecimal weight;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
