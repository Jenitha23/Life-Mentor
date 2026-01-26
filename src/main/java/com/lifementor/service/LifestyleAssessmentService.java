package com.lifementor.service;

import com.lifementor.dto.request.LifestyleAssessmentRequest;
import com.lifementor.dto.request.LifestyleAssessmentUpdateRequest;
import com.lifementor.dto.response.LifestyleAssessmentResponse;

import java.util.UUID;

public interface LifestyleAssessmentService {
    LifestyleAssessmentResponse createOrUpdateAssessment(LifestyleAssessmentRequest request);
    LifestyleAssessmentResponse getAssessment();
    LifestyleAssessmentResponse updateAssessment(LifestyleAssessmentUpdateRequest request);
    void deleteAssessment();
    boolean hasAssessment(UUID userId);
}