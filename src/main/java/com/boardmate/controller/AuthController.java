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

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/social-login")
    public ResponseEntity<SocialLoginResponse> socialLogin(
            @RequestBody SocialLoginRequest request
    ) {
        SocialLoginResponse response = userService.socialLogin(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/complete-signup")
    public ResponseEntity<Void> completeSignup(
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
