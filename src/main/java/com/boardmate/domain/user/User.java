package com.boardmate.domain.user;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, length = 255, unique = true)
    private String email;          // 카카오/구글 이메일

    @Column(length = 255)
    private String nickname;       // 회원 정보 입력 화면에서 받기

    @Column(name = "social_id", length = 500, nullable = false)
    private String socialId;       // provider가 주는 고유 ID

    @Column(name = "provider", length = 50, nullable = false)
    private String provider;       // "kakao", "google"

    @Column(length = 50)
    private String gender;         // "MALE", "FEMALE" 등

    @Column(length = 255)
    private String region;         // "서울특별시 강남구" 같은 문자열

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(length = 50)
    private String role;           // "USER", "ADMIN"

    private Boolean isActive;

    private Boolean notifyOnMeetingApproved = true;  // 내가 신청한 모임 승인 알림
    private Boolean notifyOnNewParticipant = true;   // 내가 만든 모임 참가자 알림
    private Boolean notifyOnInquiryAnswered = true;  // 문의 답변 알림

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public User(String email, String nickname, String socialId, String provider,
                String gender, String region, String profileImageUrl,
                String role, Boolean isActive) {
        this.email = email;
        this.nickname = nickname;
        this.socialId = socialId;
        this.provider = provider;
        this.gender = gender;
        this.region = region;
        this.profileImageUrl = profileImageUrl;
        this.role = role != null ? role : "USER";
        this.isActive = isActive != null ? isActive : true;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.role == null) this.role = "USER";
        if (this.isActive == null) this.isActive = true;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isProfileCompleted() {
        return this.nickname != null && this.gender != null && this.region != null;
    }

    public void updateProfile(String nickname, String gender, String region) {
        this.nickname = nickname;
        this.gender = gender;
        this.region = region;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
