package com.metropolitan.backend.service;

import com.metropolitan.backend.dto.PageResponse;
import com.metropolitan.backend.model.Inquiry;
import com.metropolitan.backend.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;

    public Inquiry createInquiry(Inquiry inquiry) {
        return inquiryRepository.save(inquiry);
    }

    public PageResponse<Inquiry> getInquiriesWithFilters(
            LocalDateTime fromDate,
            LocalDateTime toDate,
            int page,
            int limit
    ) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Inquiry> inquiryPage = inquiryRepository.findWithFilters(fromDate, toDate, pageable);
        long total = inquiryRepository.countWithFilters(fromDate, toDate);

        return PageResponse.of(inquiryPage.getContent(), total, page, limit);
    }

    public Optional<Inquiry> getInquiry(Long id) {
        return inquiryRepository.findById(id);
    }

    public void deleteInquiry(Long id) {
        if (!inquiryRepository.existsById(id)) {
            throw new RuntimeException("Inquiry not found with id: " + id);
        }
        inquiryRepository.deleteById(id);
    }
}
