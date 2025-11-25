package com.boardmate.controller;

import com.boardmate.domain.user.User;
import com.boardmate.dto.auth.SocialLoginRequest;
import com.boardmate.dto.auth.SocialLoginResponse;
import com.boardmate.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "테스트", description = "개발/테스트용 API (운영 환경에서는 비활성화)")
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final UserService userService;

    @Operation(summary = "테스트 로그인", description = "Swagger 테스트를 위한 소셜 로그인 API입니다. 백엔드 자체 JWT를 발급합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공, JWT 토큰 발급")
    @PostMapping("/login")
    public ResponseEntity<SocialLoginResponse> testLogin(
            @RequestBody SocialLoginRequest request) {
        SocialLoginResponse response = userService.socialLogin(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "전체 사용자 목록 조회", description = "개발/테스트용 API입니다. users 테이블의 모든 사용자 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
