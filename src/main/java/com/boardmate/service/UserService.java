// src/main/java/com/boardmate/service/UserService.java
package com.boardmate.service;

import com.boardmate.config.jwt.JwtTokenProvider;
import com.boardmate.domain.user.User;
import com.boardmate.dto.auth.CompleteSignupRequest;
import com.boardmate.dto.auth.SocialLoginRequest;
import com.boardmate.dto.auth.SocialLoginResponse;
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

    @Transactional
    public SocialLoginResponse socialLogin(SocialLoginRequest req) {
        Optional<User> optionalUser =
                userRepository.findByProviderAndSocialId(req.getProvider(), req.getSocialId());

        User user = optionalUser
                .map(u -> {
                    // 이메일/프로필 이미지 등 최신 정보로 업데이트
                    if (req.getEmail() != null) u.setEmail(req.getEmail());
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
        long expiresAt = System.currentTimeMillis() + 1000L * 60 * 60; // 1시간 (yml과 맞춰도 됨)

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

    @Transactional
    public void completeSignup(Long userId, CompleteSignupRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.updateProfile(req.getNickname(), req.getGender(), req.getRegion());
    }
}
