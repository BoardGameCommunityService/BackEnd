package com.boardmate.domain.meeting;

public enum MeetingSortType {
    LATEST,   // 최신순
    POPULAR,  // 인기순
    NEARBY;    // 가까운순

    public static MeetingSortType from(String value) {
        if (value == null) return LATEST;
        return MeetingSortType.valueOf(value.toUpperCase());
    }
}
