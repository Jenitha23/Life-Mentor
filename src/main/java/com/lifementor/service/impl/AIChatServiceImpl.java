package com.lifementor.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifementor.dto.request.AIChatRequest;
import com.lifementor.dto.response.AIChatHistoryResponse;
import com.lifementor.dto.response.AIChatResponse;
import com.lifementor.entity.AIChatConversation;
import com.lifementor.entity.AIChatMessage;
import com.lifementor.entity.User;
import com.lifementor.exception.ResourceNotFoundException;
import com.lifementor.exception.UnauthorizedAccessException;
import com.lifementor.exception.ValidationException;
import com.lifementor.repository.AIChatConversationRepository;
import com.lifementor.repository.AIChatMessageRepository;
import com.lifementor.repository.UserRepository;
import com.lifementor.service.AIChatService;
import com.lifementor.util.AIPromptBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AIChatServiceImpl implements AIChatService {

    private static final Logger log = LoggerFactory.getLogger(AIChatServiceImpl.class);

    private final AIChatConversationRepository conversationRepository;
    private final AIChatMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AIPromptBuilder promptBuilder;

    @Value("${app.ai.chat.api.url:}")
    private String aiChatApiUrl;

    @Value("${app.ai.chat.api.key:}")
    private String aiChatApiKey;

    @Value("${app.ai.chat.model:gpt-3.5-turbo}")
    private String aiModel;

    @Value("${app.ai.chat.max-tokens:500}")
    private int maxTokens;

    @Value("${app.ai.chat.temperature:0.7}")
    private double temperature;

    public AIChatServiceImpl(AIChatConversationRepository conversationRepository,
                             AIChatMessageRepository messageRepository,
                             UserRepository userRepository,
                             AIPromptBuilder promptBuilder) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.promptBuilder = promptBuilder;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public AIChatResponse sendMessage(UUID userId, AIChatRequest request) {
        log.info("Processing AI chat message for user: {}", userId);

        User user = getUserById(userId);
        AIChatConversation conversation = getOrCreateConversation(user, request);

        // Save user message
        AIChatMessage userMessage = new AIChatMessage(conversation, "USER", request.getMessage());
        userMessage = messageRepository.save(userMessage);

        // Generate AI response
        String aiResponseText = generateAIResponse(conversation, request.getMessage());
        int tokensUsed = estimateTokens(request.getMessage() + aiResponseText);

        // Save AI response
        AIChatMessage aiMessage = new AIChatMessage(conversation, "ASSISTANT", aiResponseText);
        aiMessage.setTokensUsed(tokensUsed);
        aiMessage.setAiModelUsed(aiModel);
        aiMessage = messageRepository.save(aiMessage);

        // Update conversation
        conversation.setUpdatedAt(LocalDateTime.now());
        if (conversation.getTitle() == null && conversation.getMessages().size() <= 2) {
            conversation.setTitle(generateConversationTitle(request.getMessage()));
        }
        conversationRepository.save(conversation);

        log.info("AI response generated for conversation: {}, tokens used: {}", 
                 conversation.getId(), tokensUsed);

        return AIChatResponse.builder()
                .conversationId(conversation.getId())
                .messageId(aiMessage.getId())
                .userMessage(request.getMessage())
                .aiResponse(aiResponseText)
                .category(conversation.getCategory())
                .timestamp(aiMessage.getCreatedAt())
                .tokensUsed(tokensUsed)
                .aiModelUsed(aiModel)
                .build();
    }

    @Override
    public AIChatHistoryResponse getConversationHistory(UUID userId, UUID conversationId) {
        AIChatConversation conversation = conversationRepository.findByIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        List<AIChatHistoryResponse.ChatMessage> messages = conversation.getMessages().stream()
                .map(msg -> new AIChatHistoryResponse.ChatMessage(
                        msg.getId(),
                        msg.getRole(),
                        msg.getContent(),
                        msg.getCreatedAt()))
                .collect(Collectors.toList());

        return AIChatHistoryResponse.builder()
                .conversationId(conversation.getId())
                .title(conversation.getTitle())
                .category(conversation.getCategory())
                .messages(messages)
                .startedAt(conversation.getCreatedAt())
                .lastMessageAt(conversation.getUpdatedAt())
                .build();
    }

    @Override
    public Page<AIChatHistoryResponse> getUserConversations(UUID userId, Pageable pageable) {
        Page<AIChatConversation> conversations = conversationRepository
                .findByUserIdAndIsActiveTrueOrderByUpdatedAtDesc(userId, pageable);

        List<AIChatHistoryResponse> responses = conversations.getContent().stream()
                .map(conv -> {
                    List<AIChatHistoryResponse.ChatMessage> lastMessages = conv.getMessages().stream()
                            .limit(2)
                            .map(msg -> new AIChatHistoryResponse.ChatMessage(
                                    msg.getId(),
                                    msg.getRole(),
                                    msg.getContent().length() > 100 ? 
                                            msg.getContent().substring(0, 100) + "..." : 
                                            msg.getContent(),
                                    msg.getCreatedAt()))
                            .collect(Collectors.toList());

                    return AIChatHistoryResponse.builder()
                            .conversationId(conv.getId())
                            .title(conv.getTitle())
                            .category(conv.getCategory())
                            .messages(lastMessages)
                            .startedAt(conv.getCreatedAt())
                            .lastMessageAt(conv.getUpdatedAt())
                            .build();
                })
                .collect(Collectors.toList());

        return new PageImpl<>(responses, pageable, conversations.getTotalElements());
    }

    @Override
    public List<AIChatHistoryResponse> getConversationsByCategory(UUID userId, String category) {
        List<AIChatConversation> conversations = conversationRepository
                .findByUserIdAndCategory(userId, category);

        return conversations.stream()
                .map(conv -> AIChatHistoryResponse.builder()
                        .conversationId(conv.getId())
                        .title(conv.getTitle())
                        .category(conv.getCategory())
                        .startedAt(conv.getCreatedAt())
                        .lastMessageAt(conv.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void deleteConversation(UUID userId, UUID conversationId) {
        AIChatConversation conversation = conversationRepository.findByIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        conversationRepository.delete(conversation);
        log.info("Deleted conversation: {} for user: {}", conversationId, userId);
    }

    @Override
    public void saveMessage(UUID userId, UUID messageId) {
        AIChatMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        if (!message.getConversation().getUser().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Not authorized to access this message");
        }

        message.setSaved(true);
        messageRepository.save(message);
        log.info("Message saved: {} for user: {}", messageId, userId);
    }

    @Override
    public AIChatResponse regenerateResponse(UUID userId, UUID conversationId, UUID messageId) {
        AIChatConversation conversation = conversationRepository.findByIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        AIChatMessage originalMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        if (!originalMessage.getConversation().getId().equals(conversationId)) {
            throw new ValidationException("Message does not belong to this conversation");
        }

        // Generate new response
        String newResponse = generateAIResponse(conversation, originalMessage.getContent());
        int tokensUsed = estimateTokens(originalMessage.getContent() + newResponse);

        // Save new response
        AIChatMessage newAiMessage = new AIChatMessage(conversation, "ASSISTANT", newResponse);
        newAiMessage.setTokensUsed(tokensUsed);
        newAiMessage.setAiModelUsed(aiModel);
        newAiMessage = messageRepository.save(newAiMessage);

        return AIChatResponse.builder()
                .conversationId(conversation.getId())
                .messageId(newAiMessage.getId())
                .userMessage(originalMessage.getContent())
                .aiResponse(newResponse)
                .category(conversation.getCategory())
                .timestamp(newAiMessage.getCreatedAt())
                .tokensUsed(tokensUsed)
                .aiModelUsed(aiModel)
                .build();
    }

    private AIChatConversation getOrCreateConversation(User user, AIChatRequest request) {
        if (request.getConversationId() != null) {
            return conversationRepository.findByIdAndUserId(request.getConversationId(), user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
        }

        AIChatConversation newConversation = new AIChatConversation(
                user,
                null, // Title will be set after first message
                request.getCategory()
        );
        return conversationRepository.save(newConversation);
    }

    private String generateAIResponse(AIChatConversation conversation, String userMessage) {
        try {
            // Build conversation context
            List<Map<String, String>> messages = conversation.getMessages().stream()
                    .map(msg -> {
                        Map<String, String> map = new HashMap<>();
                        map.put("role", msg.getRole().toLowerCase());
                        map.put("content", msg.getContent());
                        return map;
                    })
                    .collect(Collectors.toList());

            // Add system prompt if this is a new conversation
            if (messages.isEmpty()) {
                Map<String, String> systemPrompt = new HashMap<>();
                systemPrompt.put("role", "system");
                systemPrompt.put("content", promptBuilder.buildWellbeingCoachPrompt(conversation.getCategory()));
                messages.add(0, systemPrompt);
            }

            // Add current user message
            Map<String, String> currentMessage = new HashMap<>();
            currentMessage.put("role", "user");
            currentMessage.put("content", userMessage);
            messages.add(currentMessage);

            // Prepare API request
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiModel);
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("temperature", temperature);

            // Call AI API
            Map<String, Object> response = restTemplate.postForObject(
                    aiChatApiUrl,
                    requestBody,
                    Map.class
            );

            if (response != null && response.containsKey("choices")) {
                List<?> choices = (List<?>) response.get("choices");
                if (!choices.isEmpty()) {
                    Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
                    Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
                    return (String) message.get("content");
                }
            }

            // Fallback response
            return promptBuilder.getFallbackResponse(conversation.getCategory());

        } catch (Exception e) {
            log.error("Failed to generate AI response: {}", e.getMessage());
            return promptBuilder.getFallbackResponse(conversation.getCategory());
        }
    }

    private String generateConversationTitle(String firstMessage) {
        if (firstMessage.length() > 50) {
            return firstMessage.substring(0, 47) + "...";
        }
        return firstMessage;
    }

    private int estimateTokens(String text) {
        // Rough estimation: ~4 characters per token
        return (int) Math.ceil(text.length() / 4.0);
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}