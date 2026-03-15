package com.lifementor.repository;

import com.lifementor.entity.DailyCheckinResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DailyCheckinResponseRepository extends JpaRepository<DailyCheckinResponseEntity, UUID> {

    Optional<DailyCheckinResponseEntity> findByUserIdAndQuestionIdAndResponseDate(UUID userId, UUID questionId, LocalDate responseDate);

    List<DailyCheckinResponseEntity> findByUserIdAndResponseDateOrderByCreatedAtAsc(UUID userId, LocalDate responseDate);

    @Query("SELECT r FROM DailyCheckinResponseEntity r WHERE r.user.id = :userId AND r.responseDate BETWEEN :startDate AND :endDate ORDER BY r.responseDate ASC, r.question.id ASC")
    List<DailyCheckinResponseEntity> findByUserIdAndResponseDateBetweenOrderByResponseDateAscQuestionIdAsc(
            @Param("userId") UUID userId, 
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);

    @Query("SELECT r FROM DailyCheckinResponseEntity r WHERE r.user.id = :userId AND r.responseDate = :date AND r.question.category = :category")
    List<DailyCheckinResponseEntity> findByUserIdAndDateAndCategory(
            @Param("userId") UUID userId,
            @Param("date") LocalDate date,
            @Param("category") String category);

    @Query("SELECT COUNT(DISTINCT r.responseDate) FROM DailyCheckinResponseEntity r WHERE r.user.id = :userId")
    long countDistinctResponseDatesByUserId(@Param("userId") UUID userId);

    @Query("SELECT r FROM DailyCheckinResponseEntity r WHERE r.user.id = :userId AND r.responseDate = :date AND r.question.isActive = true")
    List<DailyCheckinResponseEntity> findTodaysResponses(@Param("userId") UUID userId, @Param("date") LocalDate date);

    boolean existsByUserIdAndResponseDate(UUID userId, LocalDate responseDate);

    void deleteByUserId(UUID userId);
}