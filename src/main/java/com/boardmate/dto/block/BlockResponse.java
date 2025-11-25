package com.boardmate.dto.block;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BlockResponse {
    private Long blockerId;
    private Long blockedId;
    private LocalDateTime createdAt;
}
