package com.metropolitan.backend.controller;

import com.metropolitan.backend.dto.ErrorResponse;
import com.metropolitan.backend.dto.PageResponse;
import com.metropolitan.backend.model.JobPosition;
import com.metropolitan.backend.service.JobPositionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/job-positions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminJobPositionController {

    private final JobPositionService jobPositionService;

    @GetMapping
    public ResponseEntity<PageResponse<JobPosition>> getJobPositions(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        PageResponse<JobPosition> response = jobPositionService.getJobPositionsWithFilters(
                category, fromDate, toDate, page, limit
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getJobPosition(@PathVariable Long id) {
        return jobPositionService.getJobPosition(id)
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404)
                        .body(ErrorResponse.of("Job Position not found")));
    }

    @PostMapping
    public ResponseEntity<?> createJobPosition(@Valid @RequestBody JobPosition jobPosition) {
        try {
            JobPosition created = jobPositionService.createJobPosition(jobPosition);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJobPosition(
            @PathVariable Long id,
            @Valid @RequestBody JobPosition jobPosition
    ) {
        try {
            JobPosition updated = jobPositionService.updateJobPosition(id, jobPosition);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJobPosition(@PathVariable Long id) {
        try {
            jobPositionService.deleteJobPosition(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Job Position deleted successfully");
            response.put("id", id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        FieldError fieldError = ex.getBindingResult().getFieldError();
        if (fieldError != null) {
            errors.put("message", fieldError.getDefaultMessage());
            errors.put("field", fieldError.getField());
        }
        return ResponseEntity.badRequest().body(errors);
    }
}
