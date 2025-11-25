package com.boardmate.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "follows")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dummy_id")
    private Long id;

    @Column(name = "follower_id", nullable = false)
    private Long followerId; // 팔로우하는 사람

    @Column(name = "followee_id", nullable = false)
    private Long followeeId; // 팔로우 당하는 사람

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Follow(Long followerId, Long followeeId) {
        this.followerId = followerId;
        this.followeeId = followeeId;
        this.createdAt = LocalDateTime.now();
    }
}
