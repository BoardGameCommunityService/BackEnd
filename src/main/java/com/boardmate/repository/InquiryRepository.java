package com.boardmate.repository;

import com.boardmate.domain.inquiry.Inquiry;
import com.boardmate.domain.inquiry.InquiryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    // 로그인한 사용자의 문의 목록
    Page<Inquiry> findByUser_Id(Long userId, Pageable pageable);

    Page<Inquiry> findByStatus(InquiryStatus status, Pageable pageable);

}
