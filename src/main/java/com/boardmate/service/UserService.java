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

import java.util.List;
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

        Optional<User> optionalUser = userRepository.findByEmail(req.getEmail());
        boolean alreadyRegistered = optionalUser.isPresent();

        User user;
        if (alreadyRegistered) {
            user = optionalUser.get();
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
        long expiresAt = System.currentTimeMillis() + 1000L * 60 * 60; // 1시간

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
    public void completeSignup(Long userId, CompleteSignupRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.updateProfile(req.getNickname(), req.getGender(), req.getRegion());

        if (req.getConsent() != null) {
            CompleteSignupRequest.Consent c = req.getConsent();
            // 서버 시간으로 동의 시각 저장
            user.updateConsent(c.getService(), c.getPrivacy(), c.getLocation());
        }
    }

    /**
     * 테스트/개발용: 전체 사용자 목록 조회
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
