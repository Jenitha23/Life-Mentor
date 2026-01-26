package com.lifementor.repository;

import com.lifementor.entity.LifestyleAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LifestyleAssessmentRepository extends JpaRepository<LifestyleAssessment, UUID> {

    Optional<LifestyleAssessment> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}