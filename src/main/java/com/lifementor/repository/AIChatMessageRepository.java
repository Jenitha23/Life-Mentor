package com.lifementor.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lifementor.entity.AIChatMessage;

@Repository
public interface AIChatMessageRepository extends JpaRepository<AIChatMessage, UUID> {

    List<AIChatMessage> findByConversationIdOrderByCreatedAtAsc(UUID conversationId);

    Page<AIChatMessage> findByConversationIdAndRoleOrderByCreatedAtDesc(UUID conversationId, String role, Pageable pageable);

    @Query("SELECT m FROM AIChatMessage m WHERE m.conversation.user.id = :userId AND m.createdAt BETWEEN :startDate AND :endDate")
    List<AIChatMessage> findByUserIdAndDateRange(@Param("userId") UUID userId,
                                                  @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(m) FROM AIChatMessage m WHERE m.conversation.user.id = :userId")
    long countByUserId(@Param("userId") UUID userId);

    @Query("SELECT SUM(m.tokensUsed) FROM AIChatMessage m WHERE m.conversation.user.id = :userId")
    Integer sumTokensUsedByUserId(@Param("userId") UUID userId);

    void deleteByConversationId(UUID conversationId);
}