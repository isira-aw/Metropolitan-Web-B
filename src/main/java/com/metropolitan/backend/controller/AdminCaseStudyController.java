package com.metropolitan.backend.controller;

import com.metropolitan.backend.dto.ErrorResponse;
import com.metropolitan.backend.dto.PageResponse;
import com.metropolitan.backend.model.CaseStudy;
import com.metropolitan.backend.service.CaseStudyService;
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
@RequestMapping("/api/admin/case-studies")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminCaseStudyController {

    private final CaseStudyService caseStudyService;

    @GetMapping
    public ResponseEntity<PageResponse<CaseStudy>> getCaseStudies(
            @RequestParam(required = false) String division,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        PageResponse<CaseStudy> response = caseStudyService.getCaseStudiesWithFilters(
                division, fromDate, toDate, page, limit
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCaseStudy(@PathVariable Long id) {
        return caseStudyService.getCaseStudy(id)
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404)
                        .body(ErrorResponse.of("Case Study not found")));
    }

    @PostMapping
    public ResponseEntity<?> createCaseStudy(@Valid @RequestBody CaseStudy caseStudy) {
        try {
            CaseStudy created = caseStudyService.createCaseStudy(caseStudy);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCaseStudy(
            @PathVariable Long id,
            @Valid @RequestBody CaseStudy caseStudy
    ) {
        try {
            CaseStudy updated = caseStudyService.updateCaseStudy(id, caseStudy);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCaseStudy(@PathVariable Long id) {
        try {
            caseStudyService.deleteCaseStudy(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Case study deleted successfully");
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
