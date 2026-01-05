package com.metropolitan.backend.repository;

import com.metropolitan.backend.model.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    @Query("SELECT i FROM Inquiry i WHERE " +
            "(:#{#fromDate == null} = true OR i.createdAt >= :fromDate) AND " +
            "(:#{#toDate == null} = true OR i.createdAt <= :toDate)")
    Page<Inquiry> findWithFilters(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    @Query("SELECT COUNT(i) FROM Inquiry i WHERE " +
            "(:#{#fromDate == null} = true OR i.createdAt >= :fromDate) AND " +
            "(:#{#toDate == null} = true OR i.createdAt <= :toDate)")
    long countWithFilters(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}
