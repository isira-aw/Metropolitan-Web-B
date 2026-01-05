package com.metropolitan.backend.repository;

import com.metropolitan.backend.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    @Query("SELECT n FROM News n WHERE " +
            "(:#{#fromDate == null} = true OR n.createdAt >= :fromDate) AND " +
            "(:#{#toDate == null} = true OR n.createdAt <= :toDate)")
    Page<News> findWithFilters(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    @Query("SELECT COUNT(n) FROM News n WHERE " +
            "(:#{#fromDate == null} = true OR n.createdAt >= :fromDate) AND " +
            "(:#{#toDate == null} = true OR n.createdAt <= :toDate)")
    long countWithFilters(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}
