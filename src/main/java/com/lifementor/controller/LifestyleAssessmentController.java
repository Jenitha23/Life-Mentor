package com.lifementor.controller;

import com.lifementor.dto.request.LifestyleAssessmentRequest;
import com.lifementor.dto.request.LifestyleAssessmentUpdateRequest;
import com.lifementor.dto.response.ApiResponse;
import com.lifementor.dto.response.LifestyleAssessmentResponse;
import com.lifementor.service.LifestyleAssessmentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lifestyle-assessment")
public class LifestyleAssessmentController {

    private static final Logger log = LoggerFactory.getLogger(LifestyleAssessmentController.class);

    private final LifestyleAssessmentService assessmentService;

    public LifestyleAssessmentController(LifestyleAssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createOrUpdateAssessment(
            @Valid @RequestBody LifestyleAssessmentRequest request) {
        try {
            LifestyleAssessmentResponse response = assessmentService.createOrUpdateAssessment(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Lifestyle assessment saved successfully", response));
        } catch (Exception e) {
            log.error("Failed to save lifestyle assessment: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAssessment() {
        try {
            LifestyleAssessmentResponse response = assessmentService.getAssessment();
            return ResponseEntity.ok(ApiResponse.success("Lifestyle assessment retrieved successfully", response));
        } catch (Exception e) {
            log.error("Failed to retrieve lifestyle assessment: {}", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(e.getMessage()));
            }
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve lifestyle assessment"));
        }
    }

    @PutMapping
    public ResponseEntity<ApiResponse> updateAssessment(
            @Valid @RequestBody LifestyleAssessmentUpdateRequest request) {
        try {
            LifestyleAssessmentResponse response = assessmentService.updateAssessment(request);
            return ResponseEntity.ok(ApiResponse.success("Lifestyle assessment updated successfully", response));
        } catch (Exception e) {
            log.error("Failed to update lifestyle assessment: {}", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(e.getMessage()));
            }
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteAssessment() {
        try {
            assessmentService.deleteAssessment();
            return ResponseEntity.ok(ApiResponse.success("Lifestyle assessment deleted successfully"));
        } catch (Exception e) {
            log.error("Failed to delete lifestyle assessment: {}", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(e.getMessage()));
            }
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete lifestyle assessment"));
        }
    }
}