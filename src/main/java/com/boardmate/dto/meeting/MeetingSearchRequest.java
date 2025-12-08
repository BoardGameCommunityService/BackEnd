package com.boardmate.dto.meeting;

import com.boardmate.domain.meeting.MeetingSortType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class MeetingSearchRequest {
    private String keyword;
    private String regionCode;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    private List<String> tags;     // 태그 여러개
    private Long gameId;           // 선택된 게임

    private MeetingSortType sort = MeetingSortType.LATEST;
    private Integer page = 0;
    private Integer size = 12;
}
