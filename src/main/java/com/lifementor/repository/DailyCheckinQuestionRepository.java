package com.lifementor.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lifementor.entity.DailyCheckinQuestion;

@Repository
public interface DailyCheckinQuestionRepository extends JpaRepository<DailyCheckinQuestion, UUID> {

    List<DailyCheckinQuestion> findByIsActiveTrueOrderByDisplayOrderAsc();

    List<DailyCheckinQuestion> findByCategoryAndIsActiveTrueOrderByDisplayOrderAsc(String category);

    @Query("SELECT DISTINCT q.category FROM DailyCheckinQuestion q WHERE q.isActive = true")
    List<String> findDistinctCategories();

    Optional<DailyCheckinQuestion> findByQuestion(String question);

    @Query("SELECT q FROM DailyCheckinQuestion q WHERE q.isActive = true AND q.questionType = :type ORDER BY q.displayOrder")
    List<DailyCheckinQuestion> findByQuestionType(@Param("type") String type);
}