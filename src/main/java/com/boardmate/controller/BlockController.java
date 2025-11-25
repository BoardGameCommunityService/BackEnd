package com.boardmate.controller;

import com.boardmate.domain.user.User;
import com.boardmate.dto.block.BlockRequest;
import com.boardmate.dto.block.BlockResponse;
import com.boardmate.service.BlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "차단", description = "사용자 차단 관련 API")
@RestController
@RequestMapping("/api/blocks")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService blockService;

    @Operation(summary = "사용자 차단", description = "특정 사용자를 차단합니다. JWT 인증이 필요합니다.")
    @ApiResponse(responseCode = "200", description = "차단 성공")
    @PostMapping
    public ResponseEntity<?> block(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @RequestBody BlockRequest request) {
        blockService.block(user.getId(), request.getBlockedId());
        return ResponseEntity.ok(Map.of("message", "차단 완료"));
    }

    @Operation(summary = "차단 해제", description = "차단을 해제합니다.")
    @ApiResponse(responseCode = "200", description = "차단 해제 성공")
    @DeleteMapping("/{blockedId}")
    public ResponseEntity<?> unblock(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "차단 해제할 사용자 ID", example = "1") @PathVariable Long blockedId) {
        blockService.unblock(user.getId(), blockedId);
        return ResponseEntity.ok(Map.of("message", "차단 해제 완료"));
    }

    @Operation(summary = "차단 목록 조회", description = "내가 차단한 사용자 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<BlockResponse>> getBlockedUsers(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(blockService.getBlockedUsers(user.getId()));
    }
}
