package com.boardmate.service;

import com.boardmate.domain.user.Block;
import com.boardmate.dto.block.BlockResponse;
import com.boardmate.repository.BlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockService {

    private final BlockRepository blockRepository;

    @Transactional
    public void block(Long blockerId, Long blockedId) {
        // 자기 자신을 차단할 수 없음
        if (blockerId.equals(blockedId)) {
            throw new IllegalArgumentException("자기 자신을 차단할 수 없습니다.");
        }

        // 이미 차단했는지 확인
        if (blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)) {
            throw new IllegalStateException("이미 차단한 사용자입니다.");
        }

        Block block = Block.builder()
                .blockerId(blockerId)
                .blockedId(blockedId)
                .build();

        blockRepository.save(block);
    }

    @Transactional
    public void unblock(Long blockerId, Long blockedId) {
        Block block = blockRepository.findByBlockerIdAndBlockedId(blockerId, blockedId)
                .orElseThrow(() -> new IllegalArgumentException("차단 관계가 존재하지 않습니다."));

        blockRepository.delete(block);
    }

    public List<BlockResponse> getBlockedUsers(Long userId) {
        return blockRepository.findByBlockerId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private BlockResponse toResponse(Block block) {
        return new BlockResponse(
                block.getBlockerId(),
                block.getBlockedId(),
                block.getCreatedAt());
    }
}
