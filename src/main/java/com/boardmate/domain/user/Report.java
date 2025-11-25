package com.boardmate.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId; // 신고자

    @Column(name = "target_type", nullable = false, length = 20)
    private String targetType; // USER, POST, COMMENT 등

    @Column(name = "target_id", nullable = false)
    private Long targetId; // 신고 대상 ID

    @Column(nullable = false, length = 255)
    private String reason; // 신고 사유

    @Column(columnDefinition = "TEXT")
    private String details; // 상세 내용

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, length = 20)
    private String status; // PENDING, PROCESSING, COMPLETED, REJECTED

    @Builder
    public Report(Long reporterId, String targetType, Long targetId, String reason, String details) {
        this.reporterId = reporterId;
        this.targetType = targetType;
        this.targetId = targetId;
        this.reason = reason;
        this.details = details;
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    public void updateStatus(String status) {
        this.status = status;
    }
}
