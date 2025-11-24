package com.boardmate.domain.meeting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingParticipantId implements Serializable {
    private Long meeting;
    private Long user;
}
