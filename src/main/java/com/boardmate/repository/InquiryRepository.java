package com.boardmate.repository;

import com.boardmate.domain.user.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    // 내 문의 목록
    List<Inquiry> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 답변 여부로 필터링
    List<Inquiry> findByAnswerIsNullOrderByCreatedAtDesc(); // 미답변

    List<Inquiry> findByAnswerIsNotNullOrderByCreatedAtDesc(); // 답변완료

    // 전체 문의 목록 (관리자용)
    List<Inquiry> findAllByOrderByCreatedAtDesc();

    void deleteByUserId(Long userId);
}
