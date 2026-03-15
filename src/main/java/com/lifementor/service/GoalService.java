package com.lifementor.service;

import com.lifementor.dto.request.GoalProgressUpdateRequest;
import com.lifementor.dto.request.UserGoalRequest;
import com.lifementor.dto.response.UserGoalResponse;

import java.util.List;
import java.util.UUID;

public interface GoalService {

    UserGoalResponse createGoal(UUID userId, UserGoalRequest request);

    UserGoalResponse updateGoal(UUID userId, UUID goalId, UserGoalRequest request);

    UserGoalResponse updateGoalProgress(UUID userId, GoalProgressUpdateRequest request);

    List<UserGoalResponse> getUserGoals(UUID userId);

    List<UserGoalResponse> getActiveGoals(UUID userId);

    UserGoalResponse getGoalById(UUID userId, UUID goalId);

    void deleteGoal(UUID userId, UUID goalId);

    UserGoalResponse completeGoal(UUID userId, UUID goalId);

    List<UserGoalResponse> getOverdueGoals(UUID userId);
}