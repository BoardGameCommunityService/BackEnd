package com.boardmate.controller;

import com.boardmate.domain.user.User;
import com.boardmate.dto.auth.CompleteSignupRequest;
import com.boardmate.dto.auth.SocialLoginRequest;
import com.boardmate.dto.auth.SocialLoginResponse;
import com.boardmate.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증(Auth)", description = "로그인 및 회원가입 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "NextAuth 유저 동기화", description = "NextAuth 서버에서 소셜 로그인 후 유저 정보를 Spring Boot DB에 동기화합니다. (내부 API)"
            + "<br><br>" +
            "email을 기준으로 사용자를 식별하며, 이미 가입된 사용자라면 기존 정보를 반환합니다. 신규 사용자라면 DB에 저장 후 정보를 반환합니다. "
            + "<br><br>" +
            "이미 가입된 사용자인지 여부도 함께 반환됩니다.(alreadyRegistered)"
            + "<br><br>" +
            "/api/auth/sync-from-nextauth는 동기화 API이며, /api/auth/complete-signup는 추가 정보 입력 후 회원가입 완료 API입니다.")
    @ApiResponse(responseCode = "200", description = "동기화 성공")
    @PostMapping("/sync-from-nextauth")
    public ResponseEntity<SocialLoginResponse> syncFromNextAuth(
            @RequestBody SocialLoginRequest request) {
        SocialLoginResponse response = userService.syncUserFromNextAuth(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원가입 완료", description = "소셜 로그인 후 추가 정보(닉네임, 생년월일)를 입력하여 회원가입을 완료합니다. JWT 토큰이 필요합니다."
            + "<br><br>" +
            "/api/auth/sync-from-nextauth는 동기화 API이며, /api/auth/complete-signup는 추가 정보 입력 후 회원가입 완료 API입니다.")
    @ApiResponse(responseCode = "200", description = "회원가입 완료 성공")
    @PostMapping("/complete-signup")
    public ResponseEntity<Void> completeSignup(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @RequestBody CompleteSignupRequest request) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        userService.completeSignup(user.getId(), request);
        return ResponseEntity.ok().build();
    }
}
