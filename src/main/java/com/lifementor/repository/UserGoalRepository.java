package com.lifementor.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lifementor.entity.UserGoal;

@Repository
public interface UserGoalRepository extends JpaRepository<UserGoal, UUID> {

    List<UserGoal> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<UserGoal> findByUserIdAndStatusOrderByTargetDateAsc(UUID userId, String status);

    Optional<UserGoal> findByIdAndUserId(UUID id, UUID userId);

    @Query("SELECT g FROM UserGoal g WHERE g.user.id = :userId AND g.status = 'ACTIVE' ORDER BY g.targetDate ASC")
    List<UserGoal> findActiveGoals(@Param("userId") UUID userId);

    @Query("SELECT g FROM UserGoal g WHERE g.user.id = :userId AND g.targetDate < :date AND g.status = 'ACTIVE'")
    List<UserGoal> findOverdueGoals(@Param("userId") UUID userId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(g) FROM UserGoal g WHERE g.user.id = :userId AND g.status = 'COMPLETED'")
    long countCompletedGoals(@Param("userId") UUID userId);

    void deleteByUserId(UUID userId);
}