package com.lifementor.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lifementor.dto.request.AIChatRequest;
import com.lifementor.dto.response.AIChatHistoryResponse;
import com.lifementor.dto.response.AIChatResponse;
import com.lifementor.dto.response.ApiResponse;
import com.lifementor.service.AIChatService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ai-chat")
public class AIChatController {

    private static final Logger log = LoggerFactory.getLogger(AIChatController.class);

    private final AIChatService aiChatService;

    public AIChatController(AIChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/message")
    public ResponseEntity<ApiResponse> sendMessage(
            @RequestAttribute("userId") UUID userId,
            @Valid @RequestBody AIChatRequest request) {
        try {
            log.info("Processing AI chat message for user: {}", userId);
            AIChatResponse response = aiChatService.sendMessage(userId, request);
            return ResponseEntity.ok(ApiResponse.success("Message processed successfully", response));
        } catch (Exception e) {
            log.error("Failed to process AI chat message: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse> getConversations(
            @RequestAttribute("userId") UUID userId,
            @PageableDefault(size = 10, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        try {
            Page<AIChatHistoryResponse> conversations = aiChatService.getUserConversations(userId, pageable);
            return ResponseEntity.ok(ApiResponse.success("Conversations retrieved successfully", conversations));
        } catch (Exception e) {
            log.error("Failed to retrieve conversations: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<ApiResponse> getConversationHistory(
            @RequestAttribute("userId") UUID userId,
            @PathVariable UUID conversationId) {
        try {
            AIChatHistoryResponse history = aiChatService.getConversationHistory(userId, conversationId);
            return ResponseEntity.ok(ApiResponse.success("Conversation history retrieved successfully", history));
        } catch (Exception e) {
            log.error("Failed to retrieve conversation history: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/conversations/category/{category}")
    public ResponseEntity<ApiResponse> getConversationsByCategory(
            @RequestAttribute("userId") UUID userId,
            @PathVariable String category) {
        try {
            List<AIChatHistoryResponse> conversations = aiChatService.getConversationsByCategory(userId, category);
            return ResponseEntity.ok(ApiResponse.success("Conversations retrieved successfully", conversations));
        } catch (Exception e) {
            log.error("Failed to retrieve conversations by category: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/conversations/{conversationId}")
    public ResponseEntity<ApiResponse> deleteConversation(
            @RequestAttribute("userId") UUID userId,
            @PathVariable UUID conversationId) {
        try {
            aiChatService.deleteConversation(userId, conversationId);
            return ResponseEntity.ok(ApiResponse.success("Conversation deleted successfully"));
        } catch (Exception e) {
            log.error("Failed to delete conversation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/messages/{messageId}/save")
    public ResponseEntity<ApiResponse> saveMessage(
            @RequestAttribute("userId") UUID userId,
            @PathVariable UUID messageId) {
        try {
            aiChatService.saveMessage(userId, messageId);
            return ResponseEntity.ok(ApiResponse.success("Message saved successfully"));
        } catch (Exception e) {
            log.error("Failed to save message: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/conversations/{conversationId}/messages/{messageId}/regenerate")
    public ResponseEntity<ApiResponse> regenerateResponse(
            @RequestAttribute("userId") UUID userId,
            @PathVariable UUID conversationId,
            @PathVariable UUID messageId) {
        try {
            AIChatResponse response = aiChatService.regenerateResponse(userId, conversationId, messageId);
            return ResponseEntity.ok(ApiResponse.success("Response regenerated successfully", response));
        } catch (Exception e) {
            log.error("Failed to regenerate response: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}