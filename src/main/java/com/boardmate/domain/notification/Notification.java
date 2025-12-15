package com.boardmate.domain.notification;

import com.boardmate.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String type; // REGION_MEETING, ANSWER, MEETING_APPLICATION, APPLICATION_APPROVED,
                         // APPLICATION_DENIED

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    // 관련 리소스 ID (모임 ID, 질문 ID 등)
    @Column(name = "resource_id")
    private Long resourceId;

    // 신청자 Id
    @Column(name = "related_user_id")
    private Long relatedUserId;

    @Column(nullable = false)
    private Boolean isRead = false;

    private LocalDateTime readAt;

    private LocalDateTime createdAt;

    @Builder
    public Notification(User user, String type, String title, String message, Long resourceId, Long relatedUserId) {
        this.user = user;
        this.type = type;
        this.title = title;
        this.message = message;
        this.resourceId = resourceId;
        this.relatedUserId = relatedUserId;
        this.isRead = false;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isRead == null) {
            this.isRead = false;
        }
    }

    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
}
