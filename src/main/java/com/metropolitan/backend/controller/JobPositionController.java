package com.metropolitan.backend.controller;

import com.metropolitan.backend.dto.ErrorResponse;
import com.metropolitan.backend.dto.PageResponse;
import com.metropolitan.backend.model.JobPosition;
import com.metropolitan.backend.service.JobPositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job-positions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JobPositionController {

    private final JobPositionService jobPositionService;

    @GetMapping
    public ResponseEntity<?> getActiveJobPositions(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit
    ) {
        if (page != null && limit != null) {
            PageResponse<JobPosition> response = jobPositionService.getActiveJobPositionsPaginated(page, limit);
            return ResponseEntity.ok(response);
        }
        List<JobPosition> positions = jobPositionService.getActiveJobPositions();
        return ResponseEntity.ok(positions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getJobPosition(@PathVariable Long id) {
        return jobPositionService.getJobPosition(id)
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404)
                        .body(ErrorResponse.of("Job Position not found")));
    }
}
