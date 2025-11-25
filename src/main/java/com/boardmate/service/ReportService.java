package com.boardmate.service;

import com.boardmate.domain.user.Report;
import com.boardmate.dto.report.CreateReportRequest;
import com.boardmate.dto.report.ReportResponse;
import com.boardmate.repository.MeetingRepository;
import com.boardmate.repository.ReportRepository;
import com.boardmate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final MeetingRepository meetingRepository;

    @Transactional
    public Long createReport(Long reporterId, CreateReportRequest request) {
        // target_type 검증
        validateTargetType(request.getTargetType());

        // 신고 대상이 존재하는지 검증
        validateTargetExists(request.getTargetType(), request.getTargetId());

        // 중복 신고 확인
        if (reportRepository.existsByReporterIdAndTargetTypeAndTargetId(
                reporterId, request.getTargetType(), request.getTargetId())) {
            throw new IllegalStateException("이미 신고한 대상입니다.");
        }

        Report report = Report.builder()
                .reporterId(reporterId)
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .reason(request.getReason())
                .details(request.getDetails())
                .build();

        return reportRepository.save(report).getId();
    }

    private void validateTargetType(String targetType) {
        List<String> validTypes = List.of("USER", "MEETING");
        if (!validTypes.contains(targetType)) {
            throw new IllegalArgumentException(
                    "유효하지 않은 신고 대상 타입입니다. 허용된 값: " + String.join(", ", validTypes));
        }
    }

    private void validateTargetExists(String targetType, Long targetId) {
        switch (targetType) {
            case "USER":
                if (!userRepository.existsById(targetId)) {
                    throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
                }
                break;
            case "MEETING":
                if (!meetingRepository.existsById(targetId)) {
                    throw new IllegalArgumentException("존재하지 않는 게시글(모집글)입니다.");
                }
                break;
        }
    }

    public List<ReportResponse> getMyReports(Long userId) {
        return reportRepository.findByReporterId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ReportResponse> getAllReports() {
        return reportRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateStatus(Long reportId, String status) {
        // 상태값 검증
        validateStatus(status);

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("신고를 찾을 수 없습니다."));

        report.updateStatus(status);
    }

    private void validateStatus(String status) {
        List<String> validStatuses = List.of("PENDING", "PROCESSING", "COMPLETED", "REJECTED");
        if (!validStatuses.contains(status)) {
            throw new IllegalArgumentException(
                    "유효하지 않은 상태값입니다. 허용된 값: " + String.join(", ", validStatuses));
        }
    }

    private ReportResponse toResponse(Report report) {
        return new ReportResponse(
                report.getId(),
                report.getReporterId(),
                report.getTargetType(),
                report.getTargetId(),
                report.getReason(),
                report.getDetails(),
                report.getStatus(),
                report.getCreatedAt());
    }
}
