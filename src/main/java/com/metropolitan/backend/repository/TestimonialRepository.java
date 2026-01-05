package com.metropolitan.backend.repository;

import com.metropolitan.backend.model.Testimonial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TestimonialRepository extends JpaRepository<Testimonial, Long> {
    Page<Testimonial> findByDivision(String division, Pageable pageable);
    List<Testimonial> findByDivision(String division);
    long countByDivision(String division);

    @Query("SELECT t FROM Testimonial t WHERE " +
            "(:#{#division == null} = true OR t.division = :division) AND " +
            "(:#{#fromDate == null} = true OR t.createdAt >= :fromDate) AND " +
            "(:#{#toDate == null} = true OR t.createdAt <= :toDate)")
    Page<Testimonial> findWithFilters(
            @Param("division") String division,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    @Query("SELECT COUNT(t) FROM Testimonial t WHERE " +
            "(:#{#division == null} = true OR t.division = :division) AND " +
            "(:#{#fromDate == null} = true OR t.createdAt >= :fromDate) AND " +
            "(:#{#toDate == null} = true OR t.createdAt <= :toDate)")
    long countWithFilters(
            @Param("division") String division,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}
