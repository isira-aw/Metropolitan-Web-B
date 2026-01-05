package com.metropolitan.backend.repository;

import com.metropolitan.backend.model.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    @Query("SELECT j FROM JobApplication j WHERE " +
            "(:#{#fromDate == null} = true OR j.createdAt >= :fromDate) AND " +
            "(:#{#toDate == null} = true OR j.createdAt <= :toDate)")
    Page<JobApplication> findWithFilters(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    @Query("SELECT COUNT(j) FROM JobApplication j WHERE " +
            "(:#{#fromDate == null} = true OR j.createdAt >= :fromDate) AND " +
            "(:#{#toDate == null} = true OR j.createdAt <= :toDate)")
    long countWithFilters(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}
