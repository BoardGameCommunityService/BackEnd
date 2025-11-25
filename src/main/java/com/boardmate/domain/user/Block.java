package com.boardmate.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "blocks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(Block.BlockId.class)
public class Block {

    @Id
    @Column(name = "blocker_id", nullable = false)
    private Long blockerId; // 차단한 사람

    @Id
    @Column(name = "blocked_id", nullable = false)
    private Long blockedId; // 차단당한 사람

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Block(Long blockerId, Long blockedId) {
        this.blockerId = blockerId;
        this.blockedId = blockedId;
        this.createdAt = LocalDateTime.now();
    }

    // 복합키 클래스
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BlockId implements Serializable {
        private Long blockerId;
        private Long blockedId;
    }
}
