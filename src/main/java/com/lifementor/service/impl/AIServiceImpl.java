// src/main/java/com/lifementor/service/impl/AIServiceImpl.java
package com.lifementor.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifementor.dto.response.AIFeedbackResponse;
import com.lifementor.entity.AIFeedback;
import com.lifementor.entity.LifestyleAssessment;
import com.lifementor.exception.ResourceNotFoundException;
import com.lifementor.repository.AIFeedbackRepository;
import com.lifementor.service.AIService;
import com.lifementor.service.GeminiClientService;
import com.lifementor.util.AIPromptBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AIServiceImpl implements AIService {

    private static final Logger log = LoggerFactory.getLogger(AIServiceImpl.class);

    private final AIFeedbackRepository feedbackRepository;
    private final GeminiClientService geminiClientService;
    private final AIPromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    public AIServiceImpl(AIFeedbackRepository feedbackRepository,
                         GeminiClientService geminiClientService,
                         AIPromptBuilder promptBuilder,
                         ObjectMapper objectMapper) {
        this.feedbackRepository = feedbackRepository;
        this.geminiClientService = geminiClientService;
        this.promptBuilder = promptBuilder;
        this.objectMapper = objectMapper;
    }

    @Override
    public AIFeedbackResponse generateFeedback(LifestyleAssessment assessment) {
        log.info("Generating Gemini feedback for assessment: {}", assessment.getId());

        Optional<AIFeedback> existingFeedback = feedbackRepository.findByAssessment(assessment);
        if (existingFeedback.isPresent()) {
            log.info("Feedback already exists for assessment: {}", assessment.getId());
            return mapToResponse(existingFeedback.get());
        }

        try {
            GeminiClientService.GeminiResponse geminiResponse = geminiClientService.generateContent(
                    promptBuilder.buildAssessmentFeedbackSystemPrompt(),
                    java.util.List.of(),
                    promptBuilder.buildAssessmentFeedbackUserPrompt(assessment),
                    true
            );

            AIFeedback feedback = buildFeedbackFromGeminiResponse(assessment, geminiResponse);
            feedback = feedbackRepository.save(feedback);
            log.info("Gemini feedback generated and saved: {}", feedback.getId());
            return mapToResponse(feedback);

        } catch (Exception e) {
            log.error("Failed to generate Gemini feedback: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate AI feedback: " + e.getMessage(), e);
        }
    }

    @Override
    public AIFeedbackResponse getFeedbackForAssessment(UUID assessmentId) {
        log.debug("Fetching feedback for assessment: {}", assessmentId);

        AIFeedback feedback = feedbackRepository.findByAssessmentId(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("AI feedback not found for this assessment"));

        return mapToResponse(feedback);
    }

    @Override
    public void deleteFeedback(UUID assessmentId) {
        if (!feedbackRepository.existsByAssessmentId(assessmentId)) {
            throw new ResourceNotFoundException("AI feedback not found for this assessment");
        }

        feedbackRepository.deleteByAssessmentId(assessmentId);
        log.info("Deleted AI feedback for assessment: {}", assessmentId);
    }

    private AIFeedback buildFeedbackFromGeminiResponse(LifestyleAssessment assessment,
                                                       GeminiClientService.GeminiResponse geminiResponse) throws Exception {
        JsonNode root = objectMapper.readTree(cleanJson(geminiResponse.text()));

        String riskLevel = normaliseRiskLevel(readNodeText(root, "riskLevel"));
        if (riskLevel == null) {
            riskLevel = determineRiskLevel(assessment);
        }

        return AIFeedback.builder()
                .assessment(assessment)
                .summary(defaultIfBlank(readNodeText(root, "summary"),
                        "Your lifestyle assessment was analysed successfully."))
                .positiveHighlights(defaultIfBlank(readNodeText(root, "positiveHighlights"),
                        "Completing your assessment is already a strong first step."))
                .suggestions(defaultIfBlank(readNodeText(root, "suggestions"),
                        "Choose one small healthy habit to focus on this week and track your progress."))
                .motivationalMessage(defaultIfBlank(readNodeText(root, "motivationalMessage"),
                        "Small, steady actions can lead to meaningful wellbeing improvements."))
                .aiModelVersion(geminiResponse.model())
                .riskLevel(riskLevel)
                .disclaimerShown(true)
                .build();
    }

    private String determineRiskLevel(LifestyleAssessment assessment) {
        long sleepHours = calculateSleepDuration(assessment.getSleepTime(), assessment.getWakeUpTime());

        boolean lowSleep = sleepHours < 6;
        boolean highScreenTime = assessment.getScreenTimeHours() != null
                && assessment.getScreenTimeHours().compareTo(java.math.BigDecimal.valueOf(12)) > 0;
        boolean lowMood = assessment.getMoodLevel() <= 2;
        boolean noExercise = assessment.getExerciseFrequency() == LifestyleAssessment.ExerciseFrequency.NONE;

        int riskScore = 0;
        if (lowSleep) riskScore++;
        if (highScreenTime) riskScore++;
        if (lowMood) riskScore += 2;
        if (noExercise) riskScore++;

        if (riskScore >= 3) return "HIGH";
        if (riskScore >= 2) return "MEDIUM";
        return "LOW";
    }

    private long calculateSleepDuration(LocalTime sleepTime, LocalTime wakeUpTime) {
        if (sleepTime == null || wakeUpTime == null) {
            return 0;
        }

        long hours = java.time.Duration.between(sleepTime, wakeUpTime).toHours();
        if (hours < 0) {
            hours += 24;
        }
        return hours;
    }

    private String cleanJson(String rawText) {
        String cleaned = rawText == null ? "" : rawText.trim();
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.replaceFirst("^```json\\s*", "")
                    .replaceFirst("^```\\s*", "")
                    .replaceFirst("\\s*```$", "")
                    .trim();
        }
        return cleaned;
    }

    private String readNodeText(JsonNode node, String fieldName) {
        if (node == null) {
            return null;
        }

        JsonNode value = node.get(fieldName);
        if (value == null || value.isNull()) {
            return null;
        }

        if (value.isArray()) {
            StringBuilder builder = new StringBuilder();
            for (JsonNode item : value) {
                if (!builder.isEmpty()) {
                    builder.append('\n');
                }
                builder.append(item.asText());
            }
            return builder.toString();
        }

        return value.asText();
    }

    private String normaliseRiskLevel(String riskLevel) {
        if (riskLevel == null || riskLevel.isBlank()) {
            return null;
        }

        String normalized = riskLevel.trim().toUpperCase();
        return switch (normalized) {
            case "LOW", "MEDIUM", "HIGH" -> normalized;
            default -> null;
        };
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private AIFeedbackResponse mapToResponse(AIFeedback feedback) {
        return AIFeedbackResponse.builder()
                .id(feedback.getId())
                .assessmentId(feedback.getAssessment().getId())
                .summary(feedback.getSummary())
                .positiveHighlights(feedback.getPositiveHighlights())
                .suggestions(feedback.getSuggestions())
                .motivationalMessage(feedback.getMotivationalMessage())
                .riskLevel(feedback.getRiskLevel())
                .disclaimerShown(feedback.isDisclaimerShown())
                .createdAt(feedback.getCreatedAt())
                .updatedAt(feedback.getUpdatedAt())
                .build();
    }
}
