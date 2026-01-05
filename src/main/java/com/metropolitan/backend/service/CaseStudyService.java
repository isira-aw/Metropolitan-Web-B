package com.metropolitan.backend.service;

import com.metropolitan.backend.dto.PageResponse;
import com.metropolitan.backend.model.CaseStudy;
import com.metropolitan.backend.repository.CaseStudyRepository;
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
public class CaseStudyService {

    private final CaseStudyRepository caseStudyRepository;

    public PageResponse<CaseStudy> getCaseStudies(String division, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<CaseStudy> caseStudyPage;
        long total;

        if (division != null && !division.isEmpty()) {
            caseStudyPage = caseStudyRepository.findByDivision(division, pageable);
            total = caseStudyRepository.countByDivision(division);
        } else {
            caseStudyPage = caseStudyRepository.findAll(pageable);
            total = caseStudyRepository.count();
        }

        return PageResponse.of(caseStudyPage.getContent(), total, page, limit);
    }

    public PageResponse<CaseStudy> getCaseStudiesWithFilters(
            String division,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            int page,
            int limit
    ) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<CaseStudy> caseStudyPage = caseStudyRepository.findWithFilters(division, fromDate, toDate, pageable);
        long total = caseStudyRepository.countWithFilters(division, fromDate, toDate);

        return PageResponse.of(caseStudyPage.getContent(), total, page, limit);
    }

    public Optional<CaseStudy> getCaseStudy(Long id) {
        return caseStudyRepository.findById(id);
    }

    public CaseStudy createCaseStudy(CaseStudy caseStudy) {
        return caseStudyRepository.save(caseStudy);
    }

    public CaseStudy updateCaseStudy(Long id, CaseStudy caseStudyDetails) {
        CaseStudy caseStudy = caseStudyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Case Study not found with id: " + id));

        caseStudy.setTitle(caseStudyDetails.getTitle());
        caseStudy.setDescription(caseStudyDetails.getDescription());
        caseStudy.setImage(caseStudyDetails.getImage());
        caseStudy.setDivision(caseStudyDetails.getDivision());
        caseStudy.setClient(caseStudyDetails.getClient());
        caseStudy.setLocation(caseStudyDetails.getLocation());
        caseStudy.setCompletionDate(caseStudyDetails.getCompletionDate());

        return caseStudyRepository.save(caseStudy);
    }

    public void deleteCaseStudy(Long id) {
        if (!caseStudyRepository.existsById(id)) {
            throw new RuntimeException("Case Study not found with id: " + id);
        }
        caseStudyRepository.deleteById(id);
    }
}
