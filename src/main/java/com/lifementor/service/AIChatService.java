package com.lifementor.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lifementor.dto.request.AIChatRequest;
import com.lifementor.dto.response.AIChatHistoryResponse;
import com.lifementor.dto.response.AIChatResponse;

public interface AIChatService {

    AIChatResponse sendMessage(UUID userId, AIChatRequest request);

    AIChatHistoryResponse getConversationHistory(UUID userId, UUID conversationId);

    Page<AIChatHistoryResponse> getUserConversations(UUID userId, Pageable pageable);

    List<AIChatHistoryResponse> getConversationsByCategory(UUID userId, String category);

    void deleteConversation(UUID userId, UUID conversationId);

    void saveMessage(UUID userId, UUID messageId);

    AIChatResponse regenerateResponse(UUID userId, UUID conversationId, UUID messageId);
}