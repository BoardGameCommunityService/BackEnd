package com.boardmate.domain.meeting;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeetingParticipantId implements Serializable {

    private Long meeting;
    private Long user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MeetingParticipantId)) return false;
        MeetingParticipantId that = (MeetingParticipantId) o;
        return Objects.equals(meeting, that.meeting) &&
                Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meeting, user);
    }
}