package com.metropolitan.backend.repository;

import com.metropolitan.backend.model.CaseStudy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface CaseStudyRepository extends JpaRepository<CaseStudy, Long> {
    Page<CaseStudy> findByDivision(String division, Pageable pageable);
    long countByDivision(String division);

    @Query("SELECT c FROM CaseStudy c WHERE " +
            "(:#{#division == null} = true OR c.division = :division) AND " +
            "(:#{#fromDate == null} = true OR c.createdAt >= :fromDate) AND " +
            "(:#{#toDate == null} = true OR c.createdAt <= :toDate)")
    Page<CaseStudy> findWithFilters(
            @Param("division") String division,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    @Query("SELECT COUNT(c) FROM CaseStudy c WHERE " +
            "(:#{#division == null} = true OR c.division = :division) AND " +
            "(:#{#fromDate == null} = true OR c.createdAt >= :fromDate) AND " +
            "(:#{#toDate == null} = true OR c.createdAt <= :toDate)")
    long countWithFilters(
            @Param("division") String division,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}
