package com.metropolitan.backend.controller;

import com.metropolitan.backend.dto.ErrorResponse;
import com.metropolitan.backend.dto.PageResponse;
import com.metropolitan.backend.model.JobApplication;
import com.metropolitan.backend.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/job-applications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminJobApplicationController {

    private final JobApplicationService jobApplicationService;

    @GetMapping
    public ResponseEntity<PageResponse<JobApplication>> getJobApplications(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        PageResponse<JobApplication> response = jobApplicationService.getJobApplicationsWithFilters(
                fromDate, toDate, page, limit
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getJobApplication(@PathVariable Long id) {
        return jobApplicationService.getJobApplication(id)
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404)
                        .body(ErrorResponse.of("Job Application not found")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJobApplication(@PathVariable Long id) {
        try {
            jobApplicationService.deleteJobApplication(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Job Application deleted successfully");
            response.put("id", id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
