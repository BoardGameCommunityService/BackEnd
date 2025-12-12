package com.boardmate.controller;

import com.boardmate.domain.user.User;
import com.boardmate.dto.user.UpdateUserInfoRequest;
import com.boardmate.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자", description = "사용자 정보 관리 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "본인 사용자 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다. JWT 인증 필요.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/me")
    public ResponseEntity<?> getSelfUserInfo(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getUserInfo(user.getId()));
    }

    @Operation(summary = "본인 사용자 정보 수정", description = "현재 로그인한 사용자의 정보를 수정합니다. (nickname, gender, region) JWT 인증 필요.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PutMapping("/me")
    public ResponseEntity<?> updateSelfUserInfo(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @RequestBody UpdateUserInfoRequest req) {
        return ResponseEntity.ok(userService.updateUserInfo(user.getId(), req));
    }

    @Operation(summary = "내 정보 요약", description = "사용자의 프로필 정보, 모임 통계, 알림 여부를 한 번에 조회합니다. JWT 인증 필요.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/me/summary")
    public ResponseEntity<?> getMySummary(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(userService.getMySummary(user.getId()));
    }
}
