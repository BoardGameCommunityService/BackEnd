package com.boardmate.controller;

import com.boardmate.config.jwt.JwtTokenProvider;
import com.boardmate.dto.auth.CompleteSignupRequest;
import com.boardmate.dto.auth.SocialLoginRequest;
import com.boardmate.dto.auth.SocialLoginResponse;
import com.boardmate.service.UserService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Operation(
        summary = "NextAuth 유저 동기화",
        description = "NextAuth 서버에서 소셜 로그인 후 유저 정보를 Spring Boot DB에 동기화합니다. (내부 API)"
    )
    @ApiResponse(responseCode = "200", description = "동기화 성공")
    @PostMapping("/sync-from-nextauth")
    public ResponseEntity<SocialLoginResponse> syncFromNextAuth(
            @RequestBody SocialLoginRequest request
    ) {
        SocialLoginResponse response = userService.syncUserFromNextAuth(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "회원가입 완료",
        description = "소셜 로그인 후 추가 정보(닉네임, 생년월일)를 입력하여 회원가입을 완료합니다. JWT 토큰이 필요합니다."
    )
    @ApiResponse(responseCode = "200", description = "회원가입 완료 성공")
    @PostMapping("/complete-signup")
    public ResponseEntity<Void> completeSignup(
            @Parameter(description = "JWT 토큰", example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CompleteSignupRequest request
    ) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtTokenProvider.parseToken(token).getPayload();
        Long userId = Long.parseLong(claims.getSubject());

        userService.completeSignup(userId, request);
        return ResponseEntity.ok().build();
    }
}
