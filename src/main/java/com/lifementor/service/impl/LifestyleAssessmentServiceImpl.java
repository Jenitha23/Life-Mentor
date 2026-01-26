package com.lifementor.service.impl;

import com.lifementor.dto.request.LifestyleAssessmentRequest;
import com.lifementor.dto.request.LifestyleAssessmentUpdateRequest;
import com.lifementor.dto.response.LifestyleAssessmentResponse;
import com.lifementor.entity.LifestyleAssessment;
import com.lifementor.entity.User;
import com.lifementor.exception.ResourceNotFoundException;
import com.lifementor.exception.ValidationException;
import com.lifementor.repository.LifestyleAssessmentRepository;
import com.lifementor.repository.UserRepository;
import com.lifementor.service.LifestyleAssessmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class LifestyleAssessmentServiceImpl implements LifestyleAssessmentService {

    private static final Logger log = LoggerFactory.getLogger(LifestyleAssessmentServiceImpl.class);

    private final LifestyleAssessmentRepository assessmentRepository;
    private final UserRepository userRepository;

    public LifestyleAssessmentServiceImpl(LifestyleAssessmentRepository assessmentRepository,
                                          UserRepository userRepository) {
        this.assessmentRepository = assessmentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public LifestyleAssessmentResponse createOrUpdateAssessment(LifestyleAssessmentRequest request) {
        User user = getAuthenticatedUser();

        // Check if assessment already exists for this user
        Optional<LifestyleAssessment> existingAssessment = assessmentRepository.findByUserId(user.getId());

        if (existingAssessment.isPresent()) {
            // Update existing assessment
            LifestyleAssessment assessment = existingAssessment.get();
            updateAssessmentFromRequest(assessment, request);
            assessment = assessmentRepository.save(assessment);
            log.info("Updated lifestyle assessment for user: {}", user.getId());
            return mapToResponse(assessment);
        } else {
            // Create new assessment
            validateAssessmentRequest(request);

            LifestyleAssessment assessment = LifestyleAssessment.builder()
                    .user(user)
                    .sleepTime(request.getSleepTime())
                    .wakeUpTime(request.getWakeUpTime())
                    .mealsPerDay(request.getMealsPerDay())
                    .exerciseFrequency(request.getExerciseFrequency())
                    .studyWorkHours(request.getStudyWorkHours())
                    .screenTimeHours(request.getScreenTimeHours())
                    .moodLevel(request.getMoodLevel())
                    .mentalWellbeingNote(request.getMentalWellbeingNote())
                    .build();

            assessment = assessmentRepository.save(assessment);
            log.info("Created lifestyle assessment for user: {}", user.getId());
            return mapToResponse(assessment);
        }
    }

    @Override
    public LifestyleAssessmentResponse getAssessment() {
        User user = getAuthenticatedUser();

        LifestyleAssessment assessment = assessmentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Lifestyle assessment not found"));

        return mapToResponse(assessment);
    }

    @Override
    public LifestyleAssessmentResponse updateAssessment(LifestyleAssessmentUpdateRequest request) {
        User user = getAuthenticatedUser();

        LifestyleAssessment assessment = assessmentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Lifestyle assessment not found"));

        // Update only provided fields
        if (request.getSleepTime() != null) {
            assessment.setSleepTime(request.getSleepTime());
        }

        if (request.getWakeUpTime() != null) {
            assessment.setWakeUpTime(request.getWakeUpTime());
        }

        if (request.getMealsPerDay() != null) {
            if (request.getMealsPerDay() < 1) {
                throw new ValidationException("Meals per day must be at least 1");
            }
            assessment.setMealsPerDay(request.getMealsPerDay());
        }

        if (request.getExerciseFrequency() != null) {
            assessment.setExerciseFrequency(request.getExerciseFrequency());
        }

        if (request.getStudyWorkHours() != null) {
            BigDecimal studyWorkHours = request.getStudyWorkHours();
            if (studyWorkHours.compareTo(BigDecimal.ZERO) < 0 || studyWorkHours.compareTo(new BigDecimal("24")) > 0) {
                throw new ValidationException("Study/work hours must be between 0 and 24");
            }
            assessment.setStudyWorkHours(studyWorkHours);
        }

        if (request.getScreenTimeHours() != null) {
            BigDecimal screenTimeHours = request.getScreenTimeHours();
            if (screenTimeHours.compareTo(BigDecimal.ZERO) < 0 || screenTimeHours.compareTo(new BigDecimal("24")) > 0) {
                throw new ValidationException("Screen time hours must be between 0 and 24");
            }
            assessment.setScreenTimeHours(screenTimeHours);
        }

        if (request.getMoodLevel() != null) {
            if (request.getMoodLevel() < 1 || request.getMoodLevel() > 5) {
                throw new ValidationException("Mood level must be between 1 and 5");
            }
            assessment.setMoodLevel(request.getMoodLevel());
        }

        if (request.getMentalWellbeingNote() != null) {
            assessment.setMentalWellbeingNote(request.getMentalWellbeingNote());
        }

        // Validate time range if both times are being updated
        if (request.getSleepTime() != null && request.getWakeUpTime() != null) {
            validateSleepTimeRange(request.getSleepTime(), request.getWakeUpTime());
        }

        // Validate total hours if relevant fields are being updated
        validateTotalHours(assessment);

        assessment = assessmentRepository.save(assessment);
        log.info("Updated lifestyle assessment for user: {}", user.getId());

        return mapToResponse(assessment);
    }

    @Override
    public void deleteAssessment() {
        User user = getAuthenticatedUser();

        if (!assessmentRepository.existsByUserId(user.getId())) {
            throw new ResourceNotFoundException("Lifestyle assessment not found");
        }

        assessmentRepository.deleteByUserId(user.getId());
        log.info("Deleted lifestyle assessment for user: {}", user.getId());
    }

    @Override
    public boolean hasAssessment(UUID userId) {
        return assessmentRepository.existsByUserId(userId);
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void validateAssessmentRequest(LifestyleAssessmentRequest request) {
        validateSleepTimeRange(request.getSleepTime(), request.getWakeUpTime());
        validateTotalHours(request.getExerciseFrequency(),
                request.getStudyWorkHours(),
                request.getScreenTimeHours());
    }

    private void validateTotalHours(LifestyleAssessment.ExerciseFrequency exerciseFrequency,
                                    BigDecimal studyWorkHours, BigDecimal screenTimeHours) {
        BigDecimal totalHours = (studyWorkHours != null ? studyWorkHours : BigDecimal.ZERO)
                .add(screenTimeHours != null ? screenTimeHours : BigDecimal.ZERO);

        if (totalHours.compareTo(new BigDecimal("24")) > 0) {
            throw new ValidationException("Total of study/work hours and screen time hours cannot exceed 24 hours");
        }

        // Additional validation based on exercise frequency
        if (exerciseFrequency != null && exerciseFrequency == LifestyleAssessment.ExerciseFrequency.NONE
                && totalHours.compareTo(new BigDecimal("12")) > 0) {
            throw new ValidationException("For no exercise, total activity hours should not exceed 12 hours");
        }
    }

    private void validateTotalHours(LifestyleAssessment assessment) {
        BigDecimal studyWorkHours = assessment.getStudyWorkHours();
        BigDecimal screenTimeHours = assessment.getScreenTimeHours();
        LifestyleAssessment.ExerciseFrequency exerciseFrequency = assessment.getExerciseFrequency();

        if (studyWorkHours != null && screenTimeHours != null) {
            validateTotalHours(exerciseFrequency, studyWorkHours, screenTimeHours);
        }
    }

    private void validateSleepTimeRange(LocalTime sleepTime, LocalTime wakeUpTime) {
        if (sleepTime != null && wakeUpTime != null) {
            // Check if times are equal
            if (sleepTime.equals(wakeUpTime)) {
                throw new ValidationException("Sleep time cannot be the same as wake up time");
            }

            // Calculate sleep duration (properly handles overnight sleep)
            long sleepDuration = java.time.Duration.between(sleepTime, wakeUpTime).toHours();

            // If sleep duration is negative, it means sleep time is after wake time
            // (overnight sleep like 23:00 → 07:00)
            if (sleepDuration < 0) {
                sleepDuration += 24; // Add 24 hours for overnight sleep
            }

            // Now validate the actual sleep duration
            if (sleepDuration < 6) {
                throw new ValidationException("Sleep duration should be at least 6 hours");
            }

            if (sleepDuration > 12) {
                throw new ValidationException("Sleep duration should not exceed 12 hours");
            }

            // REMOVED THE CONFUSING CHECK:
            // if (sleepTime.isAfter(wakeUpTime) || sleepTime.equals(wakeUpTime)) {
            //     throw new ValidationException("Sleep time must be before wake up time");
            // }
        }
    }

    private void updateAssessmentFromRequest(LifestyleAssessment assessment, LifestyleAssessmentRequest request) {
        assessment.setSleepTime(request.getSleepTime());
        assessment.setWakeUpTime(request.getWakeUpTime());
        assessment.setMealsPerDay(request.getMealsPerDay());
        assessment.setExerciseFrequency(request.getExerciseFrequency());
        assessment.setStudyWorkHours(request.getStudyWorkHours());
        assessment.setScreenTimeHours(request.getScreenTimeHours());
        assessment.setMoodLevel(request.getMoodLevel());
        assessment.setMentalWellbeingNote(request.getMentalWellbeingNote());
    }

    private LifestyleAssessmentResponse mapToResponse(LifestyleAssessment assessment) {
        return LifestyleAssessmentResponse.builder()
                .id(assessment.getId())
                .userId(assessment.getUser().getId())
                .sleepTime(assessment.getSleepTime())
                .wakeUpTime(assessment.getWakeUpTime())
                .mealsPerDay(assessment.getMealsPerDay())
                .exerciseFrequency(assessment.getExerciseFrequency())
                .studyWorkHours(assessment.getStudyWorkHours())
                .screenTimeHours(assessment.getScreenTimeHours())
                .moodLevel(assessment.getMoodLevel())
                .mentalWellbeingNote(assessment.getMentalWellbeingNote())
                .createdAt(assessment.getCreatedAt())
                .updatedAt(assessment.getUpdatedAt())
                .build();
    }
}