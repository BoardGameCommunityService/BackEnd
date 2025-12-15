package com.boardmate.service;

import com.boardmate.config.jwt.JwtTokenProvider;
import com.boardmate.domain.user.User;
import com.boardmate.dto.auth.CompleteSignupRequest;
import com.boardmate.dto.auth.SocialLoginRequest;
import com.boardmate.dto.auth.SocialLoginResponse;
import com.boardmate.dto.auth.TokenRefreshResponse;
import com.boardmate.dto.user.MySummary;
import com.boardmate.dto.user.UpdateUserInfoRequest;
import com.boardmate.dto.user.UserInfoResponse;
import com.boardmate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final MeetingParticipantService meetingParticipantService;
    private final NotificationService notificationService;

    /**
     * 기존에 있던 "백엔드에서 자체 JWT 발급" 소셜 로그인 로직
     * (혹시 파일에 이미 있으면 이 내용과 비교해서 합치면 됨)
     */
    @Transactional
    public SocialLoginResponse socialLogin(SocialLoginRequest req) {

        Optional<User> optionalUser = userRepository.findByEmail(req.getEmail());
        boolean alreadyRegistered = optionalUser.isPresent();

        User user;
        if (alreadyRegistered) {
            user = optionalUser.get();
            // 탈퇴한 회원 로그인 차단
            if (user.getIsActive() != null && !user.getIsActive()) {
                throw new IllegalArgumentException("This account has been deactivated");
            }
        } else {
            user = User.builder()
                    .email(req.getEmail())
                    .nickname(req.getNickname())
                    .socialId(req.getSocialId())
                    .provider(req.getProvider())
                    .profileImageUrl(req.getProfileImageUrl())
                    .role("USER")
                    .isActive(true)
                    .build();
            userRepository.save(user);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        long expiresAt = System.currentTimeMillis() + jwtTokenProvider.getAccessTokenValidityMs();

        // 리프레시 토큰 DB에 저장
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        return new SocialLoginResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole(),
                user.isProfileCompleted(),
                accessToken,
                refreshToken,
                expiresAt,
                alreadyRegistered);
    }

    /**
     * ✅ NextAuth에서만 호출하는 동기화용 메서드
     * - 유저를 생성/조회하는 로직은 socialLogin() 재사용
     * - 반환 값에서 access/refresh 토큰은 지워서 돌려줌
     * - NextAuth는 userId만 뽑아서 자기 JWT에 심어 쓰면 됨
     */
    @Transactional
    public SocialLoginResponse syncUserFromNextAuth(SocialLoginRequest req) {
        return socialLogin(req);
    }

    @Transactional
    public String completeSignup(Long userId, CompleteSignupRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.updateProfile(req.getNickname(), req.getGender(), req.getRegion());

        if (req.getConsent() != null) {
            CompleteSignupRequest.Consent c = req.getConsent();
            // 서버 시간으로 동의 시각 저장
            user.updateConsent(c.getService(), c.getPrivacy());
        }

        // 리프레시 토큰 생성 및 DB 저장
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        return refreshToken;
    }

    /**
     * 테스트/개발용: 전체 사용자 목록 조회
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 테스트/개발용: 아이디별 사용자 조회
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 테스트/개발용: 메일별 사용자 조회
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 리프레시 토큰으로 액세스 토큰 재발급 (+ 리프레시 토큰 rotation)
     */
    @Transactional
    public TokenRefreshResponse refreshToken(String refreshToken) {
        // 1) 리프레시 토큰 파싱 및 유효성 검증
        Long userId;
        try {
            userId = Long.parseLong(jwtTokenProvider.parseToken(refreshToken).getPayload().getSubject());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        // 2) 사용자 조회 및 DB에 저장된 리프레시 토큰과 비교
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRefreshToken() == null || !user.getRefreshToken().equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh token does not match or has been revoked");
        }

        // 3) 새 액세스 토큰 및 새 리프레시 토큰 발급 (rotation)
        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        long expiresAt = System.currentTimeMillis() + jwtTokenProvider.getAccessTokenValidityMs();

        // 4) 새 리프레시 토큰 DB에 저장
        user.updateRefreshToken(newRefreshToken);
        userRepository.save(user);

        return new TokenRefreshResponse(newAccessToken, newRefreshToken, expiresAt);
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserInfoResponse.from(user);
    }

    @Transactional
    public UserInfoResponse updateUserInfo(Long userId, UpdateUserInfoRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.updateUserInfo(req.getNickname(), req.getGender(), req.getRegion());
        userRepository.save(user);

        return UserInfoResponse.from(user);
    }

    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.deactivate();
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public MySummary getMySummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 모임 관련 정보
        var participationSummary = meetingParticipantService.getMyParticipationSummary(userId);

        // 알림 관련 정보
        boolean hasNewNotifications = notificationService.hasNotifications(userId);

        return MySummary.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImageUrl(user.getProfileImageUrl())
                .region(user.getRegion())
                .hostCount(participationSummary.getHostCount())
                .approvedCount(participationSummary.getApprovedCount())
                .pendingCount(participationSummary.getPendingCount())
                .hasNewNotifications(hasNewNotifications)
                .build();
    }
}
