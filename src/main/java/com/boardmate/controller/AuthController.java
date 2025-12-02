package com.boardmate.controller;

import com.boardmate.domain.user.User;
import com.boardmate.dto.auth.CompleteSignupRequest;
import com.boardmate.dto.auth.SocialLoginRequest;
import com.boardmate.dto.auth.SocialLoginResponse;
import com.boardmate.dto.auth.TokenRefreshResponse;
import com.boardmate.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import static com.boardmate.config.SecurityConfig.REFRESH_TOKEN_COOKIE_NAME;
import static com.boardmate.config.SecurityConfig.REFRESH_TOKEN_COOKIE_MAX_AGE;

@Tag(name = "인증(Auth)", description = "로그인 및 회원가입 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "NextAuth 유저 동기화", description = "NextAuth 서버에서 소셜 로그인 후 유저 정보를 Spring Boot DB에 동기화합니다."
            + "<br><br>"
            + "<b>[ 동작 방식 ]</b>"
            + "<br>• email을 기준으로 사용자 식별"
            + "<br>• 신규 사용자: DB에 저장 후 JWT 토큰 발급"
            + "<br>• 기존 사용자: 기존 정보 반환 및 새 토큰 발급"
            + "<br>• alreadyRegistered 필드로 신규/기존 구분 가능"
            + "<br><br>"
            + "<b>[ 응답 ]</b>"
            + "<br>• Body: userId, accessToken (refreshToken은 Body에 포함되지 않음)"
            + "<br>• Cookie: refreshToken (HttpOnly, Secure, 14일)"
            + "<br><br>"
            + "<b>[ 참고 ]</b>"
            + "<br>• Next.js/NextAuth 서버는 Body의 refreshToken을 사용하지 않음"
            + "<br>• 프론트엔드(브라우저)는 HttpOnly 쿠키로 자동 관리"
            + "<br>• /api/auth/complete-signup: 추가 정보 입력 후 회원가입 완료")
    @ApiResponse(responseCode = "200", description = "동기화 성공")
    @PostMapping("/sync-from-nextauth")
    public ResponseEntity<SocialLoginResponse> syncFromNextAuth(
            @RequestBody SocialLoginRequest request,
            HttpServletResponse response) {
        SocialLoginResponse loginResponse = userService.syncUserFromNextAuth(request);

        // Refresh Token을 HttpOnly 쿠키로 설정
        if (loginResponse.getRefreshToken() != null) {
            Cookie refreshTokenCookie = createRefreshTokenCookie(loginResponse.getRefreshToken());
            response.addCookie(refreshTokenCookie);

            // 응답 Body에서는 refreshToken 제거 (보안상 쿠키로만 전달)
            loginResponse.setRefreshToken(null);
        }

        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "회원가입 완료", description = "소셜 로그인 후 추가 정보를 입력하여 회원가입을 완료합니다."
            + "<br><br>"
            + "<b>[ 요구사항 ]</b>"
            + "<br>• JWT Access Token 필요 (Authorization 헤더)"
            + "<br>• 닉네임, 성별, 지역, 약관 동의 정보 필수"
            + "<br><br>"
            + "<b>[ 동작 ]</b>"
            + "<br>• 사용자 프로필 정보 업데이트"
            + "<br>• 약관 동의 정보 저장 (서버 시간 기준)"
            + "<br>• Refresh Token 생성 및 HttpOnly 쿠키로 발급"
            + "<br><br>"
            + "<b>[ 플로우 ]</b>"
            + "<br>1. /api/auth/sync-from-nextauth (소셜 로그인 동기화)"
            + "<br>2. /api/auth/complete-signup (추가 정보 입력) ← 현재 API")
    @ApiResponse(responseCode = "200", description = "회원가입 완료 성공")
    @PostMapping("/complete-signup")
    public ResponseEntity<Void> completeSignup(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @RequestBody CompleteSignupRequest request,
            HttpServletResponse response) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        String refreshToken = userService.completeSignup(user.getId(), request);

        // Refresh Token을 HttpOnly 쿠키로 설정
        Cookie refreshTokenCookie = createRefreshTokenCookie(refreshToken);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "토큰 재발급", description = "⚠️ Swagger에서는 HttpOnly 쿠키를 지원하지 않으므로 Postman을 사용하세요."
            + "<br><br>" +
            "<b>[ 동작 방식 ]</b>"
            + "<br>• 리프레시 토큰은 HttpOnly 쿠키로 자동 전송됩니다 (Request Body 없음)"
            + "<br>• 새로운 액세스 토큰과 리프레시 토큰을 발급받습니다 (Refresh Token Rotation)"
            + "<br>• 보안을 위해 매 재발급마다 리프레시 토큰도 갱신됩니다"
            + "<br><br>" +
            "<b>[ 토큰 유효 기간 ]</b>"
            + "<br>• Access Token: 1시간"
            + "<br>• Refresh Token: 14일"
            + "<br><br>" +
            "<b>[ 에러 응답 ]</b>"
            + "<br>• REFRESH_TOKEN_NOT_FOUND (401): 쿠키에 리프레시 토큰이 없음"
            + "<br>• INVALID_REFRESH_TOKEN (401): 유효하지 않거나 만료된 리프레시 토큰"
            + "<br><br>" +
            "<b>[ Postman 테스트 방법 ]</b>"
            + "<br>1. POST /api/auth/sync-from-nextauth 호출 → 쿠키 자동 저장"
            + "<br>   Body (raw JSON):"
            + "<br>   {"
            + "<br>     \"provider\": \"kakao\","
            + "<br>     \"socialId\": \"1234567890\","
            + "<br>     \"email\": \"user@example.com\","
            + "<br>     \"nickname\": \"보드러버\","
            + "<br>     \"profileImageUrl\": \"https://k.kakaocdn.net/dn/...jpg\""
            + "<br>   }"
            + "<br><br>2. POST /api/auth/refresh 호출 → 새 토큰 받음"
            + "<br>   Headers: 없음"
            + "<br>   Body: 없음 (쿠키 자동 전송)"
            + "<br>   Settings: Send cookies 활성화")
    @ApiResponse(responseCode = "200", description = "토큰 재발급 성공")
    @ApiResponse(responseCode = "401", description = "리프레시 토큰이 없거나 유효하지 않음")
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(
            @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(401)
                    .body(TokenRefreshResponse.error("REFRESH_TOKEN_NOT_FOUND"));
        }

        try {
            TokenRefreshResponse tokenResponse = userService.refreshToken(refreshToken);

            // 새 Refresh Token을 쿠키로 설정 (토큰 로테이션)
            Cookie newRefreshTokenCookie = createRefreshTokenCookie(tokenResponse.getRefreshToken());
            response.addCookie(newRefreshTokenCookie);

            // 응답에서는 refreshToken 제거 (쿠키로만 전달)
            tokenResponse.setRefreshToken(null);

            return ResponseEntity.ok(tokenResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401)
                    .body(TokenRefreshResponse.error("INVALID_REFRESH_TOKEN"));
        }
    }

    @Operation(summary = "로그아웃", description = "로그아웃 시 리프레시 토큰 쿠키를 삭제합니다."
            + "<br><br>"
            + "<b>[ 동작 ]</b>"
            + "<br>• refreshToken 쿠키 삭제 (MaxAge=0)"
            + "<br>• 클라이언트에서 accessToken도 제거 권장"
            + "<br><br>"
            + "<b>[ 참고 ]</b>"
            + "<br>• DB에 저장된 refreshToken은 자동으로 무효화되지 않음"
            + "<br>• 완전한 무효화가 필요하면 서버 측 로직 추가 필요")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // 쿠키 삭제 (MaxAge=0)
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "Strict");

        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    /**
     * HttpOnly Secure 쿠키 생성 헬퍼 메서드
     */
    private Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true); // JavaScript 접근 차단
        cookie.setSecure(true); // HTTPS에서만 전송
        cookie.setPath("/"); // 전체 경로에서 사용
        cookie.setMaxAge(REFRESH_TOKEN_COOKIE_MAX_AGE); // 14일
        cookie.setAttribute("SameSite", "Strict"); // CSRF 기본 방어

        return cookie;
    }
}
