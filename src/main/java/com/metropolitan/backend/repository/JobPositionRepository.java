package com.metropolitan.backend.repository;

import com.metropolitan.backend.model.JobPosition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JobPositionRepository extends JpaRepository<JobPosition, Long> {

    Page<JobPosition> findByCategory(String category, Pageable pageable);
    long countByCategory(String category);

    List<JobPosition> findByStatus(String status);
    Page<JobPosition> findByStatus(String status, Pageable pageable);
    long countByStatus(String status);

    @Query("SELECT j FROM JobPosition j WHERE " +
            "(:#{#category == null} = true OR j.category = :category) AND " +
            "(:#{#fromDate == null} = true OR j.createdAt >= :fromDate) AND " +
            "(:#{#toDate == null} = true OR j.createdAt <= :toDate)")
    Page<JobPosition> findWithFilters(
            @Param("category") String category,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    @Query("SELECT COUNT(j) FROM JobPosition j WHERE " +
            "(:#{#category == null} = true OR j.category = :category) AND " +
            "(:#{#fromDate == null} = true OR j.createdAt >= :fromDate) AND " +
            "(:#{#toDate == null} = true OR j.createdAt <= :toDate)")
    long countWithFilters(
            @Param("category") String category,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}
