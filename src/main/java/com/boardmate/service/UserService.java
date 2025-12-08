package com.boardmate.service;

import com.boardmate.config.jwt.JwtTokenProvider;
import com.boardmate.domain.user.User;
import com.boardmate.dto.auth.CompleteSignupRequest;
import com.boardmate.dto.auth.SocialLoginRequest;
import com.boardmate.dto.auth.SocialLoginResponse;
import com.boardmate.dto.user.NotificationSettingsResponse;
import com.boardmate.dto.user.UpdateNotificationSettingsRequest;
import com.boardmate.dto.user.UpdateProfileRequest;
import com.boardmate.dto.user.UserProfileResponse;
import com.boardmate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 기존에 있던 "백엔드에서 자체 JWT 발급" 소셜 로그인 로직
     * (혹시 파일에 이미 있으면 이 내용과 비교해서 합치면 됨)
     */
    @Transactional
    public SocialLoginResponse socialLogin(SocialLoginRequest req) {
        Optional<User> optionalUser =
                userRepository.findByProviderAndSocialId(req.getProvider(), req.getSocialId());

        User user = optionalUser
                .map(u -> {
                    // 이메일 등 최신 정보로 업데이트
                    if (req.getEmail() != null) {
                        u.setEmail(req.getEmail());
                    }
                    return u;
                })
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(req.getEmail())
                                .nickname(req.getNickname())
                                .socialId(req.getSocialId())
                                .provider(req.getProvider())
                                .profileImageUrl(req.getProfileImageUrl())
                                .role("USER")
                                .isActive(true)
                                .build()
                ));

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        long expiresAt = System.currentTimeMillis() + 1000L * 60 * 60; // 1시간

        return new SocialLoginResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole(),
                user.isProfileCompleted(),
                accessToken,
                refreshToken,
                expiresAt
        );
    }

    /**
     * NextAuth에서만 호출하는 동기화용 메서드
     *  - 유저를 생성/조회하는 로직은 socialLogin() 재사용
     *  - 반환 값에서 access/refresh 토큰은 지워서 돌려줌
     *  - NextAuth는 userId만 뽑아서 자기 JWT에 심어 쓰면 됨
     */
    @Transactional
    public SocialLoginResponse syncUserFromNextAuth(SocialLoginRequest req) {
        SocialLoginResponse response = socialLogin(req);

        return response;
    }

    @Transactional
    public void completeSignup(Long userId, CompleteSignupRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.updateProfile(req.getNickname(), req.getGender(), req.getRegion());
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return UserProfileResponse.from(user);
    }

    @Transactional
    public UserProfileResponse updateMyProfile(Long userId, UpdateProfileRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // nickname
        if (req.getNickname() != null && !req.getNickname().isBlank()) {
            user.setNickname(req.getNickname());
        }

        // gender
        if (req.getGender() != null) {
            user.setGender(req.getGender());
        }

        // region
        if (req.getRegion() != null) {
            user.setRegion(req.getRegion());
        }

        // profile image
        if (req.getProfileImageUrl() != null) {
            user.setProfileImageUrl(req.getProfileImageUrl());
        }

        return UserProfileResponse.from(user);
    }

    @Transactional(readOnly = true)
    public NotificationSettingsResponse getNotificationSettings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return NotificationSettingsResponse.builder()
                .notifyOnMeetingApproved(user.getNotifyOnMeetingApproved())
                .notifyOnNewParticipant(user.getNotifyOnNewParticipant())
                .notifyOnInquiryAnswered(user.getNotifyOnInquiryAnswered())
                .build();
    }

    @Transactional
    public void updateNotificationSettings(Long userId, UpdateNotificationSettingsRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (req.getNotifyOnMeetingApproved() != null)
            user.setNotifyOnMeetingApproved(req.getNotifyOnMeetingApproved());

        if (req.getNotifyOnNewParticipant() != null)
            user.setNotifyOnNewParticipant(req.getNotifyOnNewParticipant());

        if (req.getNotifyOnInquiryAnswered() != null)
            user.setNotifyOnInquiryAnswered(req.getNotifyOnInquiryAnswered());
    }

}
