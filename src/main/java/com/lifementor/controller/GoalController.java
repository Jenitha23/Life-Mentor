package com.lifementor.controller;

import com.lifementor.dto.request.GoalProgressUpdateRequest;
import com.lifementor.dto.request.UserGoalRequest;
import com.lifementor.dto.response.ApiResponse;
import com.lifementor.dto.response.UserGoalResponse;
import com.lifementor.service.GoalService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private static final Logger log = LoggerFactory.getLogger(GoalController.class);

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createGoal(
            @RequestAttribute("userId") UUID userId,
            @Valid @RequestBody UserGoalRequest request) {
        try {
            UserGoalResponse response = goalService.createGoal(userId, request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Goal created successfully", response));
        } catch (Exception e) {
            log.error("Failed to create goal: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getUserGoals(@RequestAttribute("userId") UUID userId) {
        try {
            List<UserGoalResponse> goals = goalService.getUserGoals(userId);
            return ResponseEntity.ok(ApiResponse.success("Goals retrieved successfully", goals));
        } catch (Exception e) {
            log.error("Failed to retrieve goals: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse> getActiveGoals(@RequestAttribute("userId") UUID userId) {
        try {
            List<UserGoalResponse> goals = goalService.getActiveGoals(userId);
            return ResponseEntity.ok(ApiResponse.success("Active goals retrieved successfully", goals));
        } catch (Exception e) {
            log.error("Failed to retrieve active goals: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse> getOverdueGoals(@RequestAttribute("userId") UUID userId) {
        try {
            List<UserGoalResponse> goals = goalService.getOverdueGoals(userId);
            return ResponseEntity.ok(ApiResponse.success("Overdue goals retrieved successfully", goals));
        } catch (Exception e) {
            log.error("Failed to retrieve overdue goals: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<ApiResponse> getGoalById(
            @RequestAttribute("userId") UUID userId,
            @PathVariable UUID goalId) {
        try {
            UserGoalResponse goal = goalService.getGoalById(userId, goalId);
            return ResponseEntity.ok(ApiResponse.success("Goal retrieved successfully", goal));
        } catch (Exception e) {
            log.error("Failed to retrieve goal: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<ApiResponse> updateGoal(
            @RequestAttribute("userId") UUID userId,
            @PathVariable UUID goalId,
            @Valid @RequestBody UserGoalRequest request) {
        try {
            UserGoalResponse response = goalService.updateGoal(userId, goalId, request);
            return ResponseEntity.ok(ApiResponse.success("Goal updated successfully", response));
        } catch (Exception e) {
            log.error("Failed to update goal: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/progress")
    public ResponseEntity<ApiResponse> updateGoalProgress(
            @RequestAttribute("userId") UUID userId,
            @Valid @RequestBody GoalProgressUpdateRequest request) {
        try {
            UserGoalResponse response = goalService.updateGoalProgress(userId, request);
            return ResponseEntity.ok(ApiResponse.success("Goal progress updated successfully", response));
        } catch (Exception e) {
            log.error("Failed to update goal progress: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{goalId}/complete")
    public ResponseEntity<ApiResponse> completeGoal(
            @RequestAttribute("userId") UUID userId,
            @PathVariable UUID goalId) {
        try {
            UserGoalResponse response = goalService.completeGoal(userId, goalId);
            return ResponseEntity.ok(ApiResponse.success("Goal marked as completed", response));
        } catch (Exception e) {
            log.error("Failed to complete goal: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<ApiResponse> deleteGoal(
            @RequestAttribute("userId") UUID userId,
            @PathVariable UUID goalId) {
        try {
            goalService.deleteGoal(userId, goalId);
            return ResponseEntity.ok(ApiResponse.success("Goal deleted successfully"));
        } catch (Exception e) {
            log.error("Failed to delete goal: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}