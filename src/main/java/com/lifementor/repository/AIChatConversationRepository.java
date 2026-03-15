package com.lifementor.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lifementor.entity.AIChatConversation;

@Repository
public interface AIChatConversationRepository extends JpaRepository<AIChatConversation, UUID> {

    List<AIChatConversation> findByUserIdOrderByUpdatedAtDesc(UUID userId);

    Page<AIChatConversation> findByUserIdAndIsActiveTrueOrderByUpdatedAtDesc(UUID userId, Pageable pageable);

    Optional<AIChatConversation> findByIdAndUserId(UUID id, UUID userId);

    @Query("SELECT c FROM AIChatConversation c WHERE c.user.id = :userId AND c.category = :category ORDER BY c.updatedAt DESC")
    List<AIChatConversation> findByUserIdAndCategory(@Param("userId") UUID userId, @Param("category") String category);

    @Query("SELECT COUNT(c) FROM AIChatConversation c WHERE c.user.id = :userId")
    long countByUserId(@Param("userId") UUID userId);

    void deleteByUserId(UUID userId);
}