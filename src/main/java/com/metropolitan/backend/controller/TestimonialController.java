package com.metropolitan.backend.controller;

import com.metropolitan.backend.dto.PageResponse;
import com.metropolitan.backend.model.Testimonial;
import com.metropolitan.backend.service.TestimonialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/testimonials")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TestimonialController {

    private final TestimonialService testimonialService;

    @GetMapping
    public ResponseEntity<?> getTestimonials(
            @RequestParam(required = false) String division,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit
    ) {
        // Support both paginated and non-paginated requests for backwards compatibility
        if (page != null && limit != null) {
            PageResponse<Testimonial> response = testimonialService.getTestimonials(division, page, limit);
            return ResponseEntity.ok(response);
        } else {
            List<Testimonial> testimonials = testimonialService.getTestimonialsNonPaginated(division);
            return ResponseEntity.ok(testimonials);
        }
    }
}
