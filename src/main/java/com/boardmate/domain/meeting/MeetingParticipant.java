package com.boardmate.domain.meeting;

import com.boardmate.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "meeting_participants",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_meeting_user",
                        columnNames = { "meeting_id", "user_id" }
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(MeetingParticipantId.class)
public class MeetingParticipant {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String status; // REQUESTED / APPROVED / REJECTED

    private String note;

    private LocalDateTime joinedAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.joinedAt = LocalDateTime.now();
        this.updatedAt = this.joinedAt;
        if (this.status == null) {
            this.status = "REQUESTED";
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static MeetingParticipant create(User user, Meeting meeting) {
        return MeetingParticipant.builder()
                .user(user)
                .meeting(meeting)
                .status("REQUESTED")
                .joinedAt(LocalDateTime.now())
                .build();
    }

    // 승인
    public void approve() {
        this.status = "APPROVED";
    }

    // 거절
    public void reject() {
        this.status = "REJECTED";
    }
}
