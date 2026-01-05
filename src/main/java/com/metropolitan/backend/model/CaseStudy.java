package com.metropolitan.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "case_studies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaseStudy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String image;

    @Column(nullable = false, length = 50)
    private String division;

    @Column(columnDefinition = "TEXT")
    private String client;

    @Column(columnDefinition = "TEXT")
    private String location;

    @Column(name = "completion_date", columnDefinition = "TEXT")
    private String completionDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
