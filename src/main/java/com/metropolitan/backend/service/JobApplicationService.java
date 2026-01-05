package com.metropolitan.backend.service;

import com.metropolitan.backend.dto.PageResponse;
import com.metropolitan.backend.model.JobApplication;
import com.metropolitan.backend.repository.JobApplicationRepository;
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
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;

    public JobApplication createJobApplication(JobApplication jobApplication) {
        return jobApplicationRepository.save(jobApplication);
    }

    public PageResponse<JobApplication> getJobApplicationsWithFilters(
            LocalDateTime fromDate,
            LocalDateTime toDate,
            int page,
            int limit
    ) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<JobApplication> jobApplicationPage = jobApplicationRepository.findWithFilters(fromDate, toDate, pageable);
        long total = jobApplicationRepository.countWithFilters(fromDate, toDate);

        return PageResponse.of(jobApplicationPage.getContent(), total, page, limit);
    }

    public Optional<JobApplication> getJobApplication(Long id) {
        return jobApplicationRepository.findById(id);
    }

    public void deleteJobApplication(Long id) {
        if (!jobApplicationRepository.existsById(id)) {
            throw new RuntimeException("Job Application not found with id: " + id);
        }
        jobApplicationRepository.deleteById(id);
    }
}
