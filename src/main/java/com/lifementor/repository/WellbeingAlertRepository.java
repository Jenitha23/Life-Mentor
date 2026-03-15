package com.lifementor.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lifementor.entity.WellbeingAlert;

@Repository
public interface WellbeingAlertRepository extends JpaRepository<WellbeingAlert, UUID> {

    List<WellbeingAlert> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<WellbeingAlert> findByUserIdAndResolvedFalseOrderByCreatedAtDesc(UUID userId);

    List<WellbeingAlert> findByUserIdAndLevelOrderByCreatedAtDesc(UUID userId, String level);

    @Query("SELECT a FROM WellbeingAlert a WHERE a.user.id = :userId AND a.createdAt >= :since")
    List<WellbeingAlert> findRecentAlerts(@Param("userId") UUID userId, @Param("since") LocalDateTime since);

    @Modifying
    @Query("UPDATE WellbeingAlert a SET a.resolved = true WHERE a.user.id = :userId")
    void resolveAllByUserId(@Param("userId") UUID userId);

    long countByUserIdAndResolvedFalse(UUID userId);

    void deleteByUserId(UUID userId);
}