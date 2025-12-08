package com.boardmate.service;

import com.boardmate.domain.inquiry.Inquiry;
import com.boardmate.domain.inquiry.InquiryStatus;
import com.boardmate.domain.user.User;
import com.boardmate.dto.inquiry.CreateInquiryRequest;
import com.boardmate.dto.inquiry.InquiryResponse;
import com.boardmate.dto.inquiry.AnswerInquiryRequest;
import com.boardmate.repository.InquiryRepository;
import com.boardmate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    /**
     * 문의 생성
     */
    @Transactional
    public InquiryResponse create(Long userId, CreateInquiryRequest req) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Inquiry inquiry = Inquiry.builder()
                .user(user)
                .title(req.getTitle())
                .content(req.getContent())
                .status(InquiryStatus.PENDING)
                .build();

        Inquiry saved = inquiryRepository.save(inquiry);
        return InquiryResponse.from(saved);
    }

    /**
     * 내가 등록한 문의 목록 조회 (마이페이지용)
     */
    @Transactional(readOnly = true)
    public Page<InquiryResponse> getMyInquiries(Long userId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return inquiryRepository.findByUser_Id(userId, pageable)
                .map(InquiryResponse::from);
    }

    /**
     * (옵션) 개별 문의 상세 조회 – 나중에 필요하면 사용
     */
    @Transactional(readOnly = true)
    public InquiryResponse getMyInquiryDetail(Long userId, Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("Inquiry not found"));

        if (!inquiry.getUser().getId().equals(userId)) {
            throw new IllegalStateException("본인이 작성한 문의만 조회할 수 있습니다.");
        }

        return InquiryResponse.from(inquiry);
    }

    /**
     * 관리자 답변 등록/수정
     */
    @Transactional
    public InquiryResponse answerInquiry(Long adminId, Long inquiryId, AnswerInquiryRequest req) {

        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("Inquiry not found"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        // Optional: 혹시 모를 보안 체크
        if (!"ADMIN".equals(admin.getRole())) {
            throw new IllegalStateException("관리자만 답변할 수 있습니다.");
        }

        inquiry.answer(req.getAnswerContent(), admin);   // expectedDate 제거된 버전

        return InquiryResponse.from(inquiry);
    }

}
