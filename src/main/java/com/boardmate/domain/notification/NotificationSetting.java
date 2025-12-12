package com.boardmate.domain.notification;

import com.boardmate.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_settings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // 알림 활성화 여부 (true: 활성화, false: 비활성화)
    @Column(nullable = false)
    private Boolean isEnabled = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public NotificationSetting(User user) {
        this.user = user;
        this.isEnabled = true;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void setEnabled(Boolean enabled) {
        this.isEnabled = enabled;
        this.updatedAt = LocalDateTime.now();
    }
}
