// src/main/java/com/lifementor/repository/AIFeedbackRepository.java
package com.lifementor.repository;

import com.lifementor.entity.AIFeedback;
import com.lifementor.entity.LifestyleAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AIFeedbackRepository extends JpaRepository<AIFeedback, UUID> {

    Optional<AIFeedback> findByAssessmentId(UUID assessmentId);

    Optional<AIFeedback> findByAssessment(LifestyleAssessment assessment);

    boolean existsByAssessmentId(UUID assessmentId);

    void deleteByAssessmentId(UUID assessmentId);
}