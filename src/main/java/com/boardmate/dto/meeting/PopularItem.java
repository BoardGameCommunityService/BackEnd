package com.boardmate.dto.meeting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PopularItem {
    private String name;
    private Long count;
}
