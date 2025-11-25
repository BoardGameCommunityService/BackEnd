package com.boardmate.service;

import com.boardmate.domain.user.Inquiry;
import com.boardmate.dto.inquiry.CreateInquiryRequest;
import com.boardmate.dto.inquiry.InquiryResponse;
import com.boardmate.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    @Transactional
    public Long createInquiry(Long userId, CreateInquiryRequest request) {
        Inquiry inquiry = Inquiry.builder()
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        return inquiryRepository.save(inquiry).getId();
    }

    public List<InquiryResponse> getMyInquiries(Long userId) {
        return inquiryRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<InquiryResponse> getAllInquiries() {
        return inquiryRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void answerInquiry(Long inquiryId, Long adminId, String answer) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의를 찾을 수 없습니다."));

        inquiry.addAnswer(adminId, answer);
    }

    private InquiryResponse toResponse(Inquiry inquiry) {
        return new InquiryResponse(
                inquiry.getId(),
                inquiry.getUserId(),
                inquiry.getTitle(),
                inquiry.getContent(),
                inquiry.getAnswer(),
                inquiry.getAnsweredBy(),
                inquiry.getCreatedAt(),
                inquiry.getAnsweredAt());
    }
}
