// src/main/java/com/lifementor/service/AIService.java
package com.lifementor.service;

import com.lifementor.dto.response.AIFeedbackResponse;
import com.lifementor.entity.LifestyleAssessment;

import java.util.UUID; // ADD THIS IMPORT

public interface AIService {

    AIFeedbackResponse generateFeedback(LifestyleAssessment assessment);

    AIFeedbackResponse getFeedbackForAssessment(UUID assessmentId); // FIXED

    void deleteFeedback(UUID assessmentId); // FIXED
}