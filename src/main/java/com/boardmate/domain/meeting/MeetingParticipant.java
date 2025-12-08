package com.boardmate.domain.meeting;

import com.boardmate.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "meeting_participants", uniqueConstraints = {
        @UniqueConstraint(name = "uk_meeting_user", columnNames = { "meeting_id", "user_id" })
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(MeetingParticipantId.class)
public class MeetingParticipant {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String note;

    private LocalDateTime joinedAt;
    private LocalDateTime updatedAt;
}
