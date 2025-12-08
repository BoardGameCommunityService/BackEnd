package com.boardmate.controller;

import com.boardmate.config.jwt.JwtTokenProvider;
import com.boardmate.dto.auth.CompleteSignupRequest;
import com.boardmate.dto.auth.SocialLoginRequest;
import com.boardmate.dto.auth.SocialLoginResponse;
import com.boardmate.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    /**
     * NextAuth 서버에서만 호출하는 유저 동기화용 API
     *  - body: SocialLoginRequest
     *  - response: SocialLoginResponse (NextAuth는 userId만 사용)
     */
    @PostMapping("/sync-from-nextauth")
    public ResponseEntity<SocialLoginResponse> syncFromNextAuth(
            @RequestBody SocialLoginRequest request) {
        SocialLoginResponse response = userService.syncUserFromNextAuth(request);
        return ResponseEntity.ok(response);
    }

    /**
     * (기존) 추가 정보 입력 완료 API – 이건 나중에 JWT 필터 붙이고 나면
     * @AuthenticationPrincipal User user 방식으로 바꿀 수 있음
     */
    @PostMapping("/complete-signup")
    public ResponseEntity<Void> completeSignup(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CompleteSignupRequest request) {
        String token = authHeader.replace("Bearer ", "");
        Claims claims = jwtTokenProvider.parseToken(token).getPayload();
        Long userId = Long.parseLong(claims.getSubject());

        userService.completeSignup(userId, request);
        return ResponseEntity.ok().build();
    }
}
