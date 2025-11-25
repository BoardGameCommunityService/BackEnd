package com.boardmate.repository;

import com.boardmate.domain.user.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    // 중복 신고 체크
    boolean existsByReporterIdAndTargetTypeAndTargetId(Long reporterId, String targetType, Long targetId);

    // 내가 한 신고 목록
    List<Report> findByReporterId(Long reporterId);

    // 특정 대상에 대한 신고 목록
    List<Report> findByTargetTypeAndTargetId(String targetType, Long targetId);

    // 상태별 신고 목록
    List<Report> findByStatus(String status);

    // 전체 신고 목록 (관리자용)
    List<Report> findAllByOrderByCreatedAtDesc();
}
