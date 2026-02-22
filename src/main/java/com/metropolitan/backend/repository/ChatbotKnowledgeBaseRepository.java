package com.metropolitan.backend.repository;

import com.metropolitan.backend.model.ChatbotKnowledgeBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatbotKnowledgeBaseRepository extends JpaRepository<ChatbotKnowledgeBase, Long> {

    List<ChatbotKnowledgeBase> findByCategory(String category);

    @Query("SELECT k FROM ChatbotKnowledgeBase k WHERE " +
           "LOWER(k.question) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
           "LOWER(k.keywords) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<ChatbotKnowledgeBase> findBySearchTerm(@Param("term") String term);

    List<ChatbotKnowledgeBase> findAllByOrderByCategoryAscCreatedAtDesc();
}
