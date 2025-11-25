package com.boardmate.repository;

import com.boardmate.domain.user.Block;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Block.BlockId> {

    // 이미 차단했는지 확인
    boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

    // 차단 관계 찾기
    Optional<Block> findByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

    // 내가 차단한 사용자 목록
    List<Block> findByBlockerId(Long blockerId);

    // 나를 차단한 사용자 목록 (필요시)
    List<Block> findByBlockedId(Long blockedId);
}
