package com.metropolitan.backend.controller;

import com.metropolitan.backend.dto.ErrorResponse;
import com.metropolitan.backend.dto.PageResponse;
import com.metropolitan.backend.model.CaseStudy;
import com.metropolitan.backend.service.CaseStudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/case-studies")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CaseStudyController {

    private final CaseStudyService caseStudyService;

    @GetMapping
    public ResponseEntity<PageResponse<CaseStudy>> getCaseStudies(
            @RequestParam(required = false) String division,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        PageResponse<CaseStudy> response = caseStudyService.getCaseStudies(division, page, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCaseStudy(@PathVariable Long id) {
        return caseStudyService.getCaseStudy(id)
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404)
                        .body(ErrorResponse.of("Case Study not found")));
    }
}
