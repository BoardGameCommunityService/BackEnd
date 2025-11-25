package com.boardmate.dto.block;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BlockRequest {
    private Long blockedId; // 차단할 사용자 ID
}
