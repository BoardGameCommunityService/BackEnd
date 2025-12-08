package com.boardmate.domain.inquiry;

import com.boardmate.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inquiries")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 문의한 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 상태 : PENDING(답변 예정), ANSWERED(답변 완료)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InquiryStatus status;

    // 실제 답변 내용 (없으면 아직 미답변)
    @Column(columnDefinition = "TEXT")
    private String answerContent;

    // 답변한 관리자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answered_by_admin_id")
    private User answeredBy;

    // 답변 완료 시각
    private LocalDateTime answeredAt;

    // 문의 등록 시각 (문의 올린 날짜)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        if (status == null) status = InquiryStatus.PENDING; // = 답변 예정
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 🔹 관리자 답변 도메인 메서드
    public void answer(String answerContent, User admin) {
        this.answerContent = answerContent;
        this.answeredBy = admin;
        this.answeredAt = LocalDateTime.now();
        this.status = InquiryStatus.ANSWERED;
    }
}