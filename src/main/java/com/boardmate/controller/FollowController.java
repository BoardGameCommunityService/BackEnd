package com.boardmate.controller;

import com.boardmate.domain.user.User;
import com.boardmate.dto.follow.FollowRequest;
import com.boardmate.dto.follow.FollowResponse;
import com.boardmate.service.FollowService;
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

@Tag(name = "팔로우", description = "팔로우 관련 API")
@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @Operation(summary = "팔로우하기", description = "다른 사용자를 팔로우합니다. JWT 인증이 필요합니다.")
    @ApiResponse(responseCode = "200", description = "팔로우 성공")
    @PostMapping
    public ResponseEntity<?> follow(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @RequestBody FollowRequest request) {
        followService.follow(user.getId(), request.getFolloweeId());
        return ResponseEntity.ok(Map.of("message", "팔로우 완료"));
    }

    @Operation(summary = "언팔로우", description = "팔로우를 취소합니다.")
    @ApiResponse(responseCode = "200", description = "언팔로우 성공")
    @DeleteMapping("/{followeeId}")
    public ResponseEntity<?> unfollow(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "언팔로우할 사용자 ID", example = "1") @PathVariable Long followeeId) {
        followService.unfollow(user.getId(), followeeId);
        return ResponseEntity.ok(Map.of("message", "언팔로우 완료"));
    }

    @Operation(summary = "팔로워 목록 조회", description = "나를 팔로우하는 사용자 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/followers")
    public ResponseEntity<List<FollowResponse>> getFollowers(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(followService.getFollowers(user.getId()));
    }

    @Operation(summary = "팔로잉 목록 조회", description = "내가 팔로우하는 사용자 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/following")
    public ResponseEntity<List<FollowResponse>> getFollowing(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(followService.getFollowing(user.getId()));
    }
}
