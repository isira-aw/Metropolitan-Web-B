package com.metropolitan.backend.service;

import com.metropolitan.backend.dto.PageResponse;
import com.metropolitan.backend.model.JobPosition;
import com.metropolitan.backend.repository.JobPositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobPositionService {

    private final JobPositionRepository jobPositionRepository;

    public PageResponse<JobPosition> getJobPositionsWithFilters(
            String category,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            int page,
            int limit
    ) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<JobPosition> jobPositionPage = jobPositionRepository.findWithFilters(category, fromDate, toDate, pageable);
        long total = jobPositionRepository.countWithFilters(category, fromDate, toDate);

        return PageResponse.of(jobPositionPage.getContent(), total, page, limit);
    }

    public List<JobPosition> getActiveJobPositions() {
        return jobPositionRepository.findByStatus("Active");
    }

    public Optional<JobPosition> getJobPosition(Long id) {
        return jobPositionRepository.findById(id);
    }

    public JobPosition createJobPosition(JobPosition jobPosition) {
        return jobPositionRepository.save(jobPosition);
    }

    public JobPosition updateJobPosition(Long id, JobPosition jobPositionDetails) {
        JobPosition jobPosition = jobPositionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job Position not found with id: " + id));

        jobPosition.setTitle(jobPositionDetails.getTitle());
        jobPosition.setDetail(jobPositionDetails.getDetail());
        jobPosition.setCategory(jobPositionDetails.getCategory());
        jobPosition.setInformation(jobPositionDetails.getInformation());
        jobPosition.setStatus(jobPositionDetails.getStatus());
        jobPosition.setImage(jobPositionDetails.getImage());

        return jobPositionRepository.save(jobPosition);
    }

    public void deleteJobPosition(Long id) {
        if (!jobPositionRepository.existsById(id)) {
            throw new RuntimeException("Job Position not found with id: " + id);
        }
        jobPositionRepository.deleteById(id);
    }
}
