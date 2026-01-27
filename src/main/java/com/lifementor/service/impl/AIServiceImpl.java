// src/main/java/com/lifementor/service/impl/AIServiceImpl.java
package com.lifementor.service.impl;

import com.lifementor.dto.response.AIFeedbackResponse;
import com.lifementor.entity.AIFeedback;
import com.lifementor.entity.LifestyleAssessment;
import com.lifementor.exception.ResourceNotFoundException;
import com.lifementor.repository.AIFeedbackRepository;
import com.lifementor.repository.LifestyleAssessmentRepository;
import com.lifementor.service.AIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AIServiceImpl implements AIService {

    private static final Logger log = LoggerFactory.getLogger(AIServiceImpl.class);

    private final AIFeedbackRepository feedbackRepository;
    private final LifestyleAssessmentRepository assessmentRepository;
    private final RestTemplate restTemplate;

    @Value("${app.ai.api.url:}")
    private String aiApiUrl;

    @Value("${app.ai.api.key:}")
    private String aiApiKey;

    @Value("${app.ai.model.version:v1.0}")
    private String aiModelVersion;

    @Value("${app.ai.fallback.enabled:true}")
    private boolean fallbackEnabled;

    public AIServiceImpl(AIFeedbackRepository feedbackRepository,
                         LifestyleAssessmentRepository assessmentRepository) {
        this.feedbackRepository = feedbackRepository;
        this.assessmentRepository = assessmentRepository;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public AIFeedbackResponse generateFeedback(LifestyleAssessment assessment) {
        log.info("Generating AI feedback for assessment: {}", assessment.getId());

        // Check if feedback already exists
        Optional<AIFeedback> existingFeedback = feedbackRepository.findByAssessment(assessment);
        if (existingFeedback.isPresent()) {
            log.info("Feedback already exists for assessment: {}", assessment.getId());
            return mapToResponse(existingFeedback.get());
        }

        try {
            // Try to get AI-generated feedback
            AIFeedback feedback = callAIApi(assessment);
            feedback = feedbackRepository.save(feedback);
            log.info("AI feedback generated and saved: {}", feedback.getId());

            return mapToResponse(feedback);

        } catch (Exception e) {
            log.error("Failed to generate AI feedback: {}", e.getMessage());

            if (fallbackEnabled) {
                log.info("Using fallback feedback generation");
                AIFeedback fallbackFeedback = generateFallbackFeedback(assessment);
                fallbackFeedback = feedbackRepository.save(fallbackFeedback);
                return mapToResponse(fallbackFeedback);
            }

            throw new RuntimeException("Failed to generate feedback and fallback is disabled");
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

    @SuppressWarnings("unchecked")
    private AIFeedback callAIApi(LifestyleAssessment assessment) {
        // If AI API is not configured, use fallback
        if (aiApiUrl == null || aiApiUrl.isEmpty()) {
            log.info("AI API not configured, using fallback");
            return generateFallbackFeedback(assessment);
        }

        try {
            // Prepare request data (ethical, non-medical, anonymized)
            Map<String, Object> requestData = new HashMap<>();

            // Lifestyle data only (no personal identifiers)
            if (assessment.getSleepTime() != null && assessment.getWakeUpTime() != null) {
                requestData.put("sleep_duration_hours", calculateSleepDuration(
                        assessment.getSleepTime(), assessment.getWakeUpTime()));
            }

            requestData.put("meals_per_day", assessment.getMealsPerDay());
            requestData.put("exercise_frequency", assessment.getExerciseFrequency().toString());
            requestData.put("study_work_hours", assessment.getStudyWorkHours());
            requestData.put("screen_time_hours", assessment.getScreenTimeHours());
            requestData.put("mood_level", assessment.getMoodLevel());

            // Add AI prompt instructions
            Map<String, Object> aiPrompt = new HashMap<>();
            aiPrompt.put("instructions", """
                You are a friendly, supportive lifestyle coach. Analyze the user's lifestyle data and provide:
                1. A brief, friendly summary of their current lifestyle
                2. Positive reinforcement (what they're doing well)
                3. 2-3 gentle, practical improvement suggestions
                4. A motivational closing message
                
                Rules:
                - Use supportive, non-judgmental language
                - Do not use medical terms or diagnoses
                - Focus on general wellbeing, not specific conditions
                - If low mood (1-2) is detected, respond with empathy and gently suggest rest
                - Always maintain ethical, encouraging tone
                - Suggestions should be small, achievable steps
                """);

            aiPrompt.put("risk_assessment_rules", """
                Assess risk level based on:
                - LOW: Normal lifestyle patterns
                - MEDIUM: Imbalanced patterns (e.g., <6h sleep, >12h screen time)
                - HIGH: Very low mood (1) or extreme patterns
                """);

            requestData.put("ai_prompt", aiPrompt);

            // Call AI API
            Map<String, Object> response = restTemplate.postForObject(
                    aiApiUrl,
                    requestData,
                    Map.class
            );

            if (response == null) {
                throw new RuntimeException("AI API returned null response");
            }

            // Parse AI response with type safety
            Object feedbackObj = response.get("feedback");
            Map<String, String> feedbackData = new HashMap<>();

            if (feedbackObj instanceof Map) {
                Map<?, ?> rawMap = (Map<?, ?>) feedbackObj;
                for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                    if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                        feedbackData.put((String) entry.getKey(), (String) entry.getValue());
                    }
                }
            }

            String riskLevel = determineRiskLevel(assessment);

            return AIFeedback.builder()
                    .assessment(assessment)
                    .summary(feedbackData.getOrDefault("summary", "Lifestyle summary"))
                    .positiveHighlights(feedbackData.getOrDefault("positive_highlights", ""))
                    .suggestions(feedbackData.getOrDefault("suggestions", "Consider small lifestyle adjustments"))
                    .motivationalMessage(feedbackData.getOrDefault("motivational_message", "You're doing great! Small steps lead to big changes."))
                    .aiModelVersion(aiModelVersion)
                    .riskLevel(riskLevel)
                    .disclaimerShown(true)
                    .build();

        } catch (Exception e) {
            log.error("AI API call failed: {}", e.getMessage());
            throw new RuntimeException("Failed to generate AI feedback: " + e.getMessage());
        }
    }

    private AIFeedback generateFallbackFeedback(LifestyleAssessment assessment) {
        log.info("Generating fallback feedback for assessment: {}", assessment.getId());

        String riskLevel = determineRiskLevel(assessment);

        // Generate context-aware fallback feedback
        String summary = generateFallbackSummary(assessment);
        String suggestions = generateFallbackSuggestions(assessment);
        String positiveHighlights = generateFallbackPositiveHighlights(assessment);
        String motivationalMessage = "Every small step counts! Keep moving forward at your own pace.";

        // Special handling for low wellbeing
        if (assessment.getMoodLevel() <= 2) {
            motivationalMessage = "It's okay to have tough days. Remember to be kind to yourself. Consider reaching out to supportive friends or professionals if needed.";
        }

        return AIFeedback.builder()
                .assessment(assessment)
                .summary(summary)
                .positiveHighlights(positiveHighlights)
                .suggestions(suggestions)
                .motivationalMessage(motivationalMessage)
                .aiModelVersion("fallback-v1.0")
                .riskLevel(riskLevel)
                .disclaimerShown(true)
                .build();
    }

    private String generateFallbackSummary(LifestyleAssessment assessment) {
        long sleepHours = calculateSleepDuration(assessment.getSleepTime(), assessment.getWakeUpTime());

        StringBuilder summary = new StringBuilder("Based on your assessment: ");

        if (sleepHours >= 7 && sleepHours <= 9) {
            summary.append("Your sleep schedule looks balanced. ");
        } else if (sleepHours < 6) {
            summary.append("You might benefit from more rest. ");
        }

        if (assessment.getMealsPerDay() >= 3) {
            summary.append("Your meal frequency supports steady energy. ");
        }

        if (assessment.getExerciseFrequency() != LifestyleAssessment.ExerciseFrequency.NONE) {
            summary.append("You're maintaining some physical activity. ");
        }

        summary.append("Let's look at ways to enhance your wellbeing.");

        return summary.toString();
    }

    private String generateFallbackSuggestions(LifestyleAssessment assessment) {
        StringBuilder suggestions = new StringBuilder();
        long sleepHours = calculateSleepDuration(assessment.getSleepTime(), assessment.getWakeUpTime());

        // Sleep suggestions
        if (sleepHours < 6) {
            suggestions.append("1. Try adding 30 minutes to your sleep routine\n");
        } else if (sleepHours > 9) {
            suggestions.append("1. Consider a consistent wake-up time\n");
        }

        // Meal suggestions
        if (assessment.getMealsPerDay() < 3) {
            suggestions.append("2. Regular meals can help maintain energy levels\n");
        }

        // Activity suggestions
        if (assessment.getExerciseFrequency() == LifestyleAssessment.ExerciseFrequency.NONE) {
            suggestions.append("3. A short daily walk can boost mood and energy\n");
        }

        // Screen time suggestions
        if (assessment.getScreenTimeHours() != null &&
                assessment.getScreenTimeHours().compareTo(java.math.BigDecimal.valueOf(8)) > 0) {
            suggestions.append("4. Try screen breaks every hour\n");
        }

        if (suggestions.isEmpty()) {
            suggestions.append("1. Maintain your current healthy habits\n");
            suggestions.append("2. Consider tracking your progress weekly\n");
            suggestions.append("3. Stay hydrated throughout the day\n");
        }

        return suggestions.toString();
    }

    private String generateFallbackPositiveHighlights(LifestyleAssessment assessment) {
        StringBuilder highlights = new StringBuilder();

        // Positive aspects based on data
        if (assessment.getMealsPerDay() >= 3) {
            highlights.append("✓ Regular meals for steady energy\n");
        }

        if (assessment.getExerciseFrequency() != LifestyleAssessment.ExerciseFrequency.NONE) {
            highlights.append("✓ Incorporating physical activity\n");
        }

        if (assessment.getMoodLevel() >= 3) {
            highlights.append("✓ Maintaining positive mood levels\n");
        }

        if (highlights.isEmpty()) {
            highlights.append("✓ Taking the first step by completing this assessment\n");
            highlights.append("✓ Awareness is the first step toward positive change\n");
        }

        return highlights.toString();
    }

    private String determineRiskLevel(LifestyleAssessment assessment) {
        // Simple risk assessment
        long sleepHours = calculateSleepDuration(assessment.getSleepTime(), assessment.getWakeUpTime());

        boolean lowSleep = sleepHours < 6;
        boolean highScreenTime = assessment.getScreenTimeHours() != null &&
                assessment.getScreenTimeHours().compareTo(java.math.BigDecimal.valueOf(12)) > 0;
        boolean lowMood = assessment.getMoodLevel() <= 2;
        boolean noExercise = assessment.getExerciseFrequency() == LifestyleAssessment.ExerciseFrequency.NONE;

        int riskScore = 0;
        if (lowSleep) riskScore++;
        if (highScreenTime) riskScore++;
        if (lowMood) riskScore += 2; // Higher weight for mood
        if (noExercise) riskScore++;

        if (riskScore >= 3) return "HIGH";
        if (riskScore >= 2) return "MEDIUM";
        return "LOW";
    }

    private long calculateSleepDuration(LocalDateTime sleepTime, LocalDateTime wakeUpTime) {
        if (sleepTime == null || wakeUpTime == null) return 0;

        java.time.Duration duration = java.time.Duration.between(sleepTime, wakeUpTime);
        long hours = duration.toHours();

        // Handle overnight sleep (sleep time after wake time)
        if (hours < 0) {
            hours += 24;
        }

        return hours;
    }

    private long calculateSleepDuration(LocalTime sleepTime, LocalTime wakeUpTime) {
        if (sleepTime == null || wakeUpTime == null) return 0;

        long hours = java.time.Duration.between(sleepTime, wakeUpTime).toHours();

        if (hours < 0) {
            hours += 24;
        }

        return hours;
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