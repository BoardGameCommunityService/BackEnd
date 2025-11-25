package com.boardmate.repository;

import com.boardmate.domain.user.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    // 이미 팔로우 중인지 확인
    boolean existsByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    // 팔로우 관계 찾기
    Optional<Follow> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    // 팔로워 목록 (나를 팔로우하는 사람들)
    List<Follow> findByFolloweeId(Long followeeId);

    // 팔로잉 목록 (내가 팔로우하는 사람들)
    List<Follow> findByFollowerId(Long followerId);

    // 팔로워 수
    long countByFolloweeId(Long followeeId);

    // 팔로잉 수
    long countByFollowerId(Long followerId);
}
