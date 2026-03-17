package com.lifementor.service.impl;

import com.lifementor.dto.request.GoalProgressUpdateRequest;
import com.lifementor.dto.request.UserGoalRequest;
import com.lifementor.dto.response.UserGoalResponse;
import com.lifementor.entity.User;
import com.lifementor.entity.UserGoal;
import com.lifementor.exception.ResourceNotFoundException;
import com.lifementor.exception.ValidationException;
import com.lifementor.repository.UserGoalRepository;
import com.lifementor.repository.UserRepository;
import com.lifementor.service.GoalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class GoalServiceImpl implements GoalService {

    private static final Logger log = LoggerFactory.getLogger(GoalServiceImpl.class);

    private final UserGoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalServiceImpl(UserGoalRepository goalRepository, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserGoalResponse createGoal(UUID userId, UserGoalRequest request) {
        log.info("Creating new goal for user: {}", userId);

        User user = getUserById(userId);

        // Validate goal type
        if (!isValidGoalType(request.getGoalType())) {
            throw new ValidationException("Invalid goal type: " + request.getGoalType());
        }

        // Validate target date is in future
        if (request.getTargetDate().isBefore(LocalDate.now())) {
            throw new ValidationException("Target date must be in the future");
        }

        UserGoal goal = new UserGoal();
        goal.setUser(user);
        goal.setGoalType(request.getGoalType());
        goal.setTargetValue(request.getTargetValue());
        goal.setTargetDate(request.getTargetDate());
        goal.setStartDate(LocalDate.now());
        goal.setCurrentValue(BigDecimal.ZERO);
        goal.setStatus("ACTIVE");
        goal.setProgressPercentage(0);
        goal.setDescription(request.getDescription());

        goal = goalRepository.save(goal);
        log.info("Goal created successfully with ID: {}", goal.getId());

        return mapToResponse(goal);
    }

    @Override
    public UserGoalResponse updateGoal(UUID userId, UUID goalId, UserGoalRequest request) {
        log.info("Updating goal: {} for user: {}", goalId, userId);

        UserGoal goal = getGoalByIdAndUser(goalId, userId);

        // Update fields
        if (request.getGoalType() != null) {
            if (!isValidGoalType(request.getGoalType())) {
                throw new ValidationException("Invalid goal type: " + request.getGoalType());
            }
            goal.setGoalType(request.getGoalType());
        }

        if (request.getTargetValue() != null) {
            goal.setTargetValue(request.getTargetValue());
        }

        if (request.getTargetDate() != null) {
            if (request.getTargetDate().isBefore(LocalDate.now())) {
                throw new ValidationException("Target date must be in the future");
            }
            goal.setTargetDate(request.getTargetDate());
        }

        if (request.getDescription() != null) {
            goal.setDescription(request.getDescription());
        }

        // Recalculate progress
        updateProgressPercentage(goal);

        goal = goalRepository.save(goal);
        log.info("Goal updated successfully");

        return mapToResponse(goal);
    }

    @Override
    public UserGoalResponse updateGoalProgress(UUID userId, GoalProgressUpdateRequest request) {
        log.info("Updating progress for goal: {} for user: {}", request.getGoalId(), userId);

        UserGoal goal = getGoalByIdAndUser(request.getGoalId(), userId);

        if (!"ACTIVE".equals(goal.getStatus())) {
            throw new ValidationException("Cannot update progress for a non-active goal");
        }

        goal.setCurrentValue(request.getCurrentValue());
        
        if (request.getNotes() != null) {
            goal.setNotes(request.getNotes());
        }

        // Update progress percentage
        updateProgressPercentage(goal);

        // Check if goal is completed
        if (goal.getProgressPercentage() >= 100) {
            goal.setStatus("COMPLETED");
            log.info("Goal completed! User: {}, Goal: {}", userId, goal.getId());
        }

        goal = goalRepository.save(goal);

        return mapToResponse(goal);
    }

    @Override
    public List<UserGoalResponse> getUserGoals(UUID userId) {
        log.debug("Fetching all goals for user: {}", userId);

        return goalRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserGoalResponse> getActiveGoals(UUID userId) {
        log.debug("Fetching active goals for user: {}", userId);

        return goalRepository.findActiveGoals(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserGoalResponse getGoalById(UUID userId, UUID goalId) {
        UserGoal goal = getGoalByIdAndUser(goalId, userId);
        return mapToResponse(goal);
    }

    @Override
    public void deleteGoal(UUID userId, UUID goalId) {
        log.info("Deleting goal: {} for user: {}", goalId, userId);

        UserGoal goal = getGoalByIdAndUser(goalId, userId);
        goalRepository.delete(goal);

        log.info("Goal deleted successfully");
    }

    @Override
    public UserGoalResponse completeGoal(UUID userId, UUID goalId) {
        log.info("Manually completing goal: {} for user: {}", goalId, userId);

        UserGoal goal = getGoalByIdAndUser(goalId, userId);

        goal.setStatus("COMPLETED");
        goal.setProgressPercentage(100);
        goal.setCurrentValue(goal.getTargetValue());

        goal = goalRepository.save(goal);

        return mapToResponse(goal);
    }

    @Override
    public List<UserGoalResponse> getOverdueGoals(UUID userId) {
        log.debug("Fetching overdue goals for user: {}", userId);

        return goalRepository.findOverdueGoals(userId, LocalDate.now()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void updateProgressPercentage(UserGoal goal) {
        if (goal.getTargetValue() != null && goal.getTargetValue().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal progress = goal.getCurrentValue()
                    .divide(goal.getTargetValue(), 2, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100));
            
            goal.setProgressPercentage(progress.intValue());
        }
    }

    private boolean isValidGoalType(String goalType) {
        return List.of(
            "SLEEP_IMPROVEMENT",
            "EXERCISE_INCREASE",
            "MOOD_IMPROVEMENT",
            "MEAL_COUNT",
            "WATER_INTAKE",
            "SCREEN_TIME_REDUCTION",
            "STRESS_REDUCTION",
            "PRODUCTIVITY_INCREASE"
        ).contains(goalType);
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private UserGoal getGoalByIdAndUser(UUID goalId, UUID userId) {
        return goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
    }

    private UserGoalResponse mapToResponse(UserGoal goal) {
        return UserGoalResponse.builder()
                .id(goal.getId())
                .goalType(goal.getGoalType())
                .targetValue(goal.getTargetValue())
                .currentValue(goal.getCurrentValue())
                .targetDate(goal.getTargetDate())
                .startDate(goal.getStartDate())
                .status(goal.getStatus())
                .progressPercentage(goal.getProgressPercentage())
                .description(goal.getDescription())
                .createdAt(goal.getCreatedAt())
                .updatedAt(goal.getUpdatedAt())
                .build();
    }
}