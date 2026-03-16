package com.lifementor.service.impl;

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
import com.lifementor.service.GeminiClientService;
import com.lifementor.util.AIPromptBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AIChatServiceImpl implements AIChatService {

    private static final Logger log = LoggerFactory.getLogger(AIChatServiceImpl.class);

    private final AIChatConversationRepository conversationRepository;
    private final AIChatMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final AIPromptBuilder promptBuilder;
    private final GeminiClientService geminiClientService;

    public AIChatServiceImpl(AIChatConversationRepository conversationRepository,
                             AIChatMessageRepository messageRepository,
                             UserRepository userRepository,
                             AIPromptBuilder promptBuilder,
                             GeminiClientService geminiClientService) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.promptBuilder = promptBuilder;
        this.geminiClientService = geminiClientService;
    }

    @Override
    public AIChatResponse sendMessage(UUID userId, AIChatRequest request) {
        log.info("Processing AI chat message for user: {}", userId);

        User user = getUserById(userId);
        AIChatConversation conversation = getOrCreateConversation(user, request);

        messageRepository.save(new AIChatMessage(conversation, "USER", request.getMessage()));

        GeminiClientService.GeminiResponse geminiResponse =
                generateAIResponse(conversation, request.getMessage());
        String aiResponseText = geminiResponse.text();
        int tokensUsed = geminiResponse.totalTokens() != null
                ? geminiResponse.totalTokens()
                : estimateTokens(request.getMessage() + aiResponseText);

        AIChatMessage aiMessage = new AIChatMessage(conversation, "ASSISTANT", aiResponseText);
        aiMessage.setTokensUsed(tokensUsed);
        aiMessage.setAiModelUsed(geminiResponse.model());
        aiMessage = messageRepository.save(aiMessage);

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
                .aiModelUsed(geminiResponse.model())
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
                                    msg.getContent().length() > 100
                                            ? msg.getContent().substring(0, 100) + "..."
                                            : msg.getContent(),
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

        GeminiClientService.GeminiResponse geminiResponse =
                generateAIResponse(conversation, originalMessage.getContent());
        String newResponse = geminiResponse.text();
        int tokensUsed = geminiResponse.totalTokens() != null
                ? geminiResponse.totalTokens()
                : estimateTokens(originalMessage.getContent() + newResponse);

        AIChatMessage newAiMessage = new AIChatMessage(conversation, "ASSISTANT", newResponse);
        newAiMessage.setTokensUsed(tokensUsed);
        newAiMessage.setAiModelUsed(geminiResponse.model());
        newAiMessage = messageRepository.save(newAiMessage);

        return AIChatResponse.builder()
                .conversationId(conversation.getId())
                .messageId(newAiMessage.getId())
                .userMessage(originalMessage.getContent())
                .aiResponse(newResponse)
                .category(conversation.getCategory())
                .timestamp(newAiMessage.getCreatedAt())
                .tokensUsed(tokensUsed)
                .aiModelUsed(geminiResponse.model())
                .build();
    }

    private AIChatConversation getOrCreateConversation(User user, AIChatRequest request) {
        if (request.getConversationId() != null) {
            return conversationRepository.findByIdAndUserId(request.getConversationId(), user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
        }

        AIChatConversation newConversation = new AIChatConversation(
                user,
                null,
                request.getCategory()
        );
        return conversationRepository.save(newConversation);
    }

    private GeminiClientService.GeminiResponse generateAIResponse(AIChatConversation conversation, String userMessage) {
        try {
            List<AIChatMessage> conversationMessages = conversation.getMessages();
            int historySize = conversationMessages.size();

            if (historySize > 0) {
                AIChatMessage lastMessage = conversationMessages.get(historySize - 1);
                if ("USER".equalsIgnoreCase(lastMessage.getRole())
                        && userMessage.equals(lastMessage.getContent())) {
                    historySize--;
                }
            }

            List<GeminiClientService.ChatTurn> history = conversationMessages.stream()
                    .limit(historySize)
                    .filter(msg -> msg.getContent() != null && !msg.getContent().isBlank())
                    .map(msg -> new GeminiClientService.ChatTurn(msg.getRole(), msg.getContent()))
                    .collect(Collectors.toList());

            return geminiClientService.generateContent(
                    promptBuilder.buildWellbeingCoachPrompt(conversation.getCategory()),
                    history,
                    userMessage,
                    false
            );

        } catch (Exception e) {
            log.error("Failed to generate AI response: {}", e.getMessage(), e);
            return new GeminiClientService.GeminiResponse(
                    promptBuilder.getFallbackResponse(conversation.getCategory()),
                    estimateTokens(userMessage),
                    geminiClientService.getConfiguredModel()
            );
        }
    }

    private String generateConversationTitle(String firstMessage) {
        if (firstMessage.length() > 50) {
            return firstMessage.substring(0, 47) + "...";
        }
        return firstMessage;
    }

    private int estimateTokens(String text) {
        return (int) Math.ceil(text.length() / 4.0);
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
