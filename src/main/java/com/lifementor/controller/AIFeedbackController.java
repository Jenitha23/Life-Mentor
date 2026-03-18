// src/main/java/com/lifementor/controller/AIFeedbackController.java
package com.lifementor.controller;

import com.lifementor.dto.response.ApiResponse;
import com.lifementor.dto.response.AIFeedbackResponse;
import com.lifementor.entity.LifestyleAssessment;
import com.lifementor.exception.ResourceNotFoundException;
import com.lifementor.repository.LifestyleAssessmentRepository;
import com.lifementor.service.AIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/ai-feedback")
public class AIFeedbackController {

    private static final Logger log = LoggerFactory.getLogger(AIFeedbackController.class);

    private final AIService aiService;
    private final LifestyleAssessmentRepository assessmentRepository;

    public AIFeedbackController(AIService aiService,
                                LifestyleAssessmentRepository assessmentRepository) {
        this.aiService = aiService;
        this.assessmentRepository = assessmentRepository;
        log.info("AIFeedbackController initialized with AIService: {}",
                aiService != null ? aiService.getClass().getSimpleName() : "NULL");
    }

    @GetMapping("/assessment/{assessmentId}")
    public ResponseEntity<ApiResponse> getFeedbackForAssessment(
          @PathVariable("assessmentId") UUID assessmentId) {
        try {
            log.info("Fetching AI feedback for assessment: {}", assessmentId);

            // First check if assessment exists
            if (!assessmentRepository.existsById(assessmentId)) {
                log.warn("Assessment not found: {}", assessmentId);
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Assessment not found with ID: " + assessmentId));
            }

            AIFeedbackResponse feedback = aiService.getFeedbackForAssessment(assessmentId);

            log.info("Successfully retrieved AI feedback for assessment: {}", assessmentId);
            return ResponseEntity.ok(ApiResponse.success("AI feedback retrieved successfully", feedback));

        } catch (ResourceNotFoundException e) {
            log.warn("AI feedback not found for assessment: {}. Error: {}", assessmentId, e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));

        } catch (Exception e) {
            log.error("Failed to retrieve AI feedback for assessment {}: {}",
                    assessmentId, e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve AI feedback: " + e.getMessage()));
        }
    }

    @PostMapping("/generate/{assessmentId}")
    public ResponseEntity<ApiResponse> generateFeedback(
            @PathVariable("assessmentId") UUID assessmentId) {
        try {
            log.info("Manual AI feedback generation requested for assessment: {}", assessmentId);

            // Get the assessment
            LifestyleAssessment assessment = assessmentRepository.findById(assessmentId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Assessment not found with ID: " + assessmentId));

            log.info("Found assessment for user: {}. Generating AI feedback...",
                    assessment.getUser().getId());

            // Check if feedback already exists
            try {
                AIFeedbackResponse existingFeedback = aiService.getFeedbackForAssessment(assessmentId);
                log.info("AI feedback already exists for assessment: {}. Feedback ID: {}",
                        assessmentId, existingFeedback.getId());

                return ResponseEntity.ok(ApiResponse.success(
                        "AI feedback already exists for this assessment",
                        existingFeedback
                ));
            } catch (ResourceNotFoundException e) {
                // Feedback doesn't exist, proceed to generate
                log.info("No existing AI feedback found. Generating new feedback...");
            }

            // Generate new feedback
            AIFeedbackResponse feedback = aiService.generateFeedback(assessment);

            log.info("✅ AI feedback generated successfully for assessment: {}. Feedback ID: {}",
                    assessmentId, feedback.getId());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("AI feedback generated successfully", feedback));

        } catch (ResourceNotFoundException e) {
            log.error("Assessment not found for feedback generation: {}. Error: {}",
                    assessmentId, e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));

        } catch (Exception e) {
            log.error("Failed to generate AI feedback for assessment {}: {}",
                    assessmentId, e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to generate AI feedback: " + e.getMessage()));
        }
    }

    @PostMapping("/test-generate")
    public ResponseEntity<ApiResponse> testGenerateFeedback() {
        try {
            log.info("Testing AI feedback generation...");

            // Find any assessment to test with
            LifestyleAssessment assessment = assessmentRepository.findAll()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("No assessments found for testing"));

            log.info("Testing with assessment ID: {}", assessment.getId());

            // Generate feedback
            AIFeedbackResponse feedback = aiService.generateFeedback(assessment);

            log.info("✅ Test AI feedback generation successful! Feedback ID: {}", feedback.getId());

            return ResponseEntity.ok(ApiResponse.success(
                    "Test AI feedback generation successful",
                    feedback
            ));

        } catch (ResourceNotFoundException e) {
            log.warn("No assessments available for testing");
            return ResponseEntity.ok(ApiResponse.success(
                    "No assessments available for testing. Create an assessment first."
            ));

        } catch (Exception e) {
            log.error("Test AI feedback generation failed: {}", e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Test failed: " + e.getMessage()));
        }
    }

    @DeleteMapping("/assessment/{assessmentId}")
    public ResponseEntity<ApiResponse> deleteFeedback(
            @PathVariable("assessmentId") UUID assessmentId ){
        try {
            log.info("Deleting AI feedback for assessment: {}", assessmentId);

            // Check if assessment exists first
            if (!assessmentRepository.existsById(assessmentId)) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Assessment not found with ID: " + assessmentId));
            }

            // Delete feedback
            aiService.deleteFeedback(assessmentId);

            log.info("✅ AI feedback deleted successfully for assessment: {}", assessmentId);
            return ResponseEntity.ok(ApiResponse.success("AI feedback deleted successfully"));

        } catch (ResourceNotFoundException e) {
            log.warn("AI feedback not found for deletion: {}. Error: {}", assessmentId, e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));

        } catch (Exception e) {
            log.error("Failed to delete AI feedback for assessment {}: {}",
                    assessmentId, e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete AI feedback: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse> checkHealth() {
        try {
            log.info("Checking AI feedback service health...");

            boolean isHealthy = aiService != null;

            String statusMessage = isHealthy
                    ? "✅ AI Feedback Service is healthy and available"
                    : "❌ AI Feedback Service is not available";

            var healthData = new java.util.HashMap<String, Object>();
            healthData.put("serviceAvailable", isHealthy);
            healthData.put("serviceClass", aiService != null ? aiService.getClass().getName() : "null");
            healthData.put("timestamp", java.time.LocalDateTime.now());

            return ResponseEntity.ok(ApiResponse.success(statusMessage, healthData));

        } catch (Exception e) {
            log.error("Health check failed: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.error("AI Feedback Service health check failed"));
        }
    }
}