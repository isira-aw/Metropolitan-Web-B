package com.metropolitan.backend.service;

import com.metropolitan.backend.dto.PageResponse;
import com.metropolitan.backend.model.Testimonial;
import com.metropolitan.backend.repository.TestimonialRepository;
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
public class TestimonialService {

    private final TestimonialRepository testimonialRepository;

    public PageResponse<Testimonial> getTestimonials(String division, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Testimonial> testimonialPage;
        long total;

        if (division != null && !division.isEmpty()) {
            testimonialPage = testimonialRepository.findByDivision(division, pageable);
            total = testimonialRepository.countByDivision(division);
        } else {
            testimonialPage = testimonialRepository.findAll(pageable);
            total = testimonialRepository.count();
        }

        return PageResponse.of(testimonialPage.getContent(), total, page, limit);
    }

    public PageResponse<Testimonial> getTestimonialsWithFilters(
            String division,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            int page,
            int limit
    ) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Testimonial> testimonialPage = testimonialRepository.findWithFilters(division, fromDate, toDate, pageable);
        long total = testimonialRepository.countWithFilters(division, fromDate, toDate);

        return PageResponse.of(testimonialPage.getContent(), total, page, limit);
    }

    public List<Testimonial> getTestimonialsNonPaginated(String division) {
        if (division != null && !division.isEmpty()) {
            return testimonialRepository.findByDivision(division);
        }
        return testimonialRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public Optional<Testimonial> getTestimonial(Long id) {
        return testimonialRepository.findById(id);
    }

    public Testimonial createTestimonial(Testimonial testimonial) {
        return testimonialRepository.save(testimonial);
    }

    public Testimonial updateTestimonial(Long id, Testimonial testimonialDetails) {
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Testimonial not found with id: " + id));

        testimonial.setContent(testimonialDetails.getContent());
        testimonial.setAuthor(testimonialDetails.getAuthor());
        testimonial.setRole(testimonialDetails.getRole());
        testimonial.setDivision(testimonialDetails.getDivision());

        return testimonialRepository.save(testimonial);
    }

    public void deleteTestimonial(Long id) {
        if (!testimonialRepository.existsById(id)) {
            throw new RuntimeException("Testimonial not found with id: " + id);
        }
        testimonialRepository.deleteById(id);
    }
}
