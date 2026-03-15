package com.lifementor.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifementor.dto.request.DailyCheckinBatchRequest;
import com.lifementor.dto.request.DailyCheckinRequest;
import com.lifementor.dto.response.DailyCheckinAnalyticsResponse;
import com.lifementor.dto.response.DailyCheckinResponse;
import com.lifementor.dto.response.WellbeingAlertResponse;
import com.lifementor.entity.DailyCheckinQuestion;
import com.lifementor.entity.DailyCheckinResponseEntity;
import com.lifementor.entity.User;
import com.lifementor.entity.WellbeingAlert;
import com.lifementor.exception.ResourceNotFoundException;
import com.lifementor.exception.ValidationException;
import com.lifementor.repository.DailyCheckinQuestionRepository;
import com.lifementor.repository.DailyCheckinResponseRepository;
import com.lifementor.repository.UserRepository;
import com.lifementor.repository.WellbeingAlertRepository;
import com.lifementor.service.DailyCheckinService;
import com.lifementor.util.WellbeingAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DailyCheckinServiceImpl implements DailyCheckinService {

    private static final Logger log = LoggerFactory.getLogger(DailyCheckinServiceImpl.class);

    private final DailyCheckinResponseRepository responseRepository;
    private final DailyCheckinQuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final WellbeingAlertRepository alertRepository;
    private final WellbeingAnalyzer wellbeingAnalyzer;
    private final ObjectMapper objectMapper;

    public DailyCheckinServiceImpl(DailyCheckinResponseRepository responseRepository,
                                   DailyCheckinQuestionRepository questionRepository,
                                   UserRepository userRepository,
                                   WellbeingAlertRepository alertRepository,
                                   WellbeingAnalyzer wellbeingAnalyzer) {
        this.responseRepository = responseRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.alertRepository = alertRepository;
        this.wellbeingAnalyzer = wellbeingAnalyzer;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<DailyCheckinResponse> submitDailyCheckin(UUID userId, DailyCheckinBatchRequest request) {
        log.info("Submitting daily check-in batch for user: {}", userId);

        User user = getUserById(userId);
        LocalDate today = LocalDate.now();

        // Check if user already submitted today
        if (responseRepository.existsByUserIdAndResponseDate(userId, today)) {
            throw new ValidationException("You have already completed today's check-in");
        }

        List<DailyCheckinResponseEntity> savedResponses = new ArrayList<>();

        for (DailyCheckinRequest req : request.getResponses()) {
            DailyCheckinQuestion question = questionRepository.findById(req.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + req.getQuestionId()));

            // Validate answer based on question type
            validateAnswer(question, req.getAnswer());

            // Create and save response
            DailyCheckinResponseEntity response = new DailyCheckinResponseEntity();
            response.setUser(user);
            response.setQuestion(question);
            response.setAnswer(req.getAnswer());
            response.setResponseDate(today);
            response.setMetadata(req.getMetadata());

            savedResponses.add(responseRepository.save(response));
        }

        log.info("Successfully submitted {} responses for user: {}", savedResponses.size(), userId);

        // Check for wellbeing alerts
        checkAndCreateAlerts(userId, savedResponses);

        return savedResponses.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DailyCheckinResponse submitSingleResponse(UUID userId, DailyCheckinRequest request) {
        log.debug("Submitting single response for user: {}", userId);

        User user = getUserById(userId);
        LocalDate today = LocalDate.now();

        DailyCheckinQuestion question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + request.getQuestionId()));

        // Check if already answered today
        Optional<DailyCheckinResponseEntity> existing = 
                responseRepository.findByUserIdAndQuestionIdAndResponseDate(userId, request.getQuestionId(), today);

        if (existing.isPresent()) {
            throw new ValidationException("You have already answered this question today");
        }

        validateAnswer(question, request.getAnswer());

        DailyCheckinResponseEntity response = new DailyCheckinResponseEntity();
        response.setUser(user);
        response.setQuestion(question);
        response.setAnswer(request.getAnswer());
        response.setResponseDate(today);
        response.setMetadata(request.getMetadata());

        response = responseRepository.save(response);

        return mapToDTO(response);
    }

    @Override
    public List<DailyCheckinResponse> getTodaysCheckin(UUID userId) {
        log.debug("Fetching today's check-in for user: {}", userId);

        LocalDate today = LocalDate.now();
        List<DailyCheckinResponseEntity> responses = 
                responseRepository.findByUserIdAndResponseDateOrderByCreatedAtAsc(userId, today);

        return responses.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DailyCheckinResponse> getCheckinByDate(UUID userId, LocalDate date) {
        log.debug("Fetching check-in for user: {} on date: {}", userId, date);

        List<DailyCheckinResponseEntity> responses = 
                responseRepository.findByUserIdAndResponseDateOrderByCreatedAtAsc(userId, date);

        return responses.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DailyCheckinAnalyticsResponse getAnalytics(UUID userId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating analytics for user: {} from {} to {}", userId, startDate, endDate);

        List<DailyCheckinResponseEntity> responses = 
                responseRepository.findByUserIdAndResponseDateBetweenOrderByResponseDateAscQuestionIdAsc(
                        userId, startDate, endDate);

        // Calculate streaks
        int currentStreak = calculateCurrentStreak(userId);
        int longestStreak = calculateLongestStreak(userId);
        long totalCheckins = responseRepository.countDistinctResponseDatesByUserId(userId);

        // Group by category and analyze
        Map<String, List<DailyCheckinResponseEntity>> byCategory = responses.stream()
                .collect(Collectors.groupingBy(r -> r.getQuestion().getCategory()));

        Map<String, DailyCheckinAnalyticsResponse.CategoryAnalytics> categoryAnalytics = new HashMap<>();

        for (Map.Entry<String, List<DailyCheckinResponseEntity>> entry : byCategory.entrySet()) {
            String category = entry.getKey();
            List<DailyCheckinResponseEntity> categoryResponses = entry.getValue();

            Map<String, Object> insights = wellbeingAnalyzer.analyzeCategory(category, categoryResponses);
            double avgValue = calculateAverageForCategory(category, categoryResponses);

            categoryAnalytics.put(category, 
                    new DailyCheckinAnalyticsResponse.CategoryAnalytics(
                            category, categoryResponses.size(), insights, avgValue));
        }

        // Calculate mood trend
        List<DailyCheckinAnalyticsResponse.MoodTrend> moodTrend = calculateMoodTrend(responses);

        // Recent check-ins summary
        List<DailyCheckinAnalyticsResponse.CheckinSummary> recentCheckins = getRecentCheckinsSummary(userId);

        return DailyCheckinAnalyticsResponse.builder()
                .userId(userId)
                .totalCheckins((int) totalCheckins)
                .currentStreak(currentStreak)
                .longestStreak(longestStreak)
                .lastCheckinDate(responses.isEmpty() ? null : responses.get(responses.size() - 1).getResponseDate())
                .categoryAnalytics(categoryAnalytics)
                .moodTrend(moodTrend)
                .recentCheckins(recentCheckins)
                .build();
    }

    @Override
    public List<WellbeingAlertResponse> checkWellbeingAlerts(UUID userId) {
        log.info("Checking wellbeing alerts for user: {}", userId);

        User user = getUserById(userId);
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(7);

        List<DailyCheckinResponseEntity> last7Days = 
                responseRepository.findByUserIdAndResponseDateBetweenOrderByResponseDateAscQuestionIdAsc(
                        userId, sevenDaysAgo, today);

        List<WellbeingAlert> alerts = wellbeingAnalyzer.generateAlerts(user, last7Days);
        
        // Save alerts to database
        for (WellbeingAlert alert : alerts) {
            alertRepository.save(alert);
        }

        return alerts.stream()
                .map(this::mapAlertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasCompletedTodayCheckin(UUID userId) {
        LocalDate today = LocalDate.now();
        return responseRepository.existsByUserIdAndResponseDate(userId, today);
    }

    @Override
    public int getCurrentStreak(UUID userId) {
        return calculateCurrentStreak(userId);
    }

    @Override
    public void deleteCheckinResponse(UUID userId, UUID responseId) {
        DailyCheckinResponseEntity response = responseRepository.findById(responseId)
                .orElseThrow(() -> new ResourceNotFoundException("Response not found"));

        if (!response.getUser().getId().equals(userId)) {
            throw new ValidationException("Not authorized to delete this response");
        }

        responseRepository.delete(response);
        log.info("Deleted check-in response: {} for user: {}", responseId, userId);
    }

    private void validateAnswer(DailyCheckinQuestion question, String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            throw new ValidationException("Answer cannot be empty");
        }

        switch (question.getQuestionType()) {
            case "YES_NO":
                if (!answer.equalsIgnoreCase("yes") && !answer.equalsIgnoreCase("no")) {
                    throw new ValidationException("Answer must be 'yes' or 'no'");
                }
                break;

            case "SCALE":
                try {
                    int value = Integer.parseInt(answer);
                    if (question.getOptions() != null) {
                        Map<String, Integer> options = objectMapper.readValue(
                                question.getOptions(), new TypeReference<Map<String, Integer>>() {});
                        int min = options.getOrDefault("min", 1);
                        int max = options.getOrDefault("max", 10);
                        if (value < min || value > max) {
                            throw new ValidationException("Value must be between " + min + " and " + max);
                        }
                    }
                } catch (NumberFormatException e) {
                    throw new ValidationException("Answer must be a number");
                } catch (Exception e) {
                    log.error("Error parsing options: {}", e.getMessage());
                }
                break;
        }
    }

    private int calculateCurrentStreak(UUID userId) {
        int streak = 0;
        LocalDate date = LocalDate.now();

        while (true) {
            if (responseRepository.existsByUserIdAndResponseDate(userId, date)) {
                streak++;
                date = date.minusDays(1);
            } else {
                break;
            }
        }

        return streak;
    }

    private int calculateLongestStreak(UUID userId) {
        List<DailyCheckinResponseEntity> responses = responseRepository
                .findByUserIdAndResponseDateBetweenOrderByResponseDateAscQuestionIdAsc(
                        userId, 
                        LocalDate.now().minusYears(1), 
                        LocalDate.now()
                );
        
        List<LocalDate> checkinDates = responses.stream()
                .map(DailyCheckinResponseEntity::getResponseDate)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        if (checkinDates.isEmpty()) {
            return 0;
        }

        int longestStreak = 1;
        int currentStreak = 1;

        for (int i = 1; i < checkinDates.size(); i++) {
            if (ChronoUnit.DAYS.between(checkinDates.get(i - 1), checkinDates.get(i)) == 1) {
                currentStreak++;
                longestStreak = Math.max(longestStreak, currentStreak);
            } else {
                currentStreak = 1;
            }
        }

        return longestStreak;
    }

    private List<DailyCheckinAnalyticsResponse.MoodTrend> calculateMoodTrend(
            List<DailyCheckinResponseEntity> responses) {

        return responses.stream()
                .filter(r -> "MOOD".equals(r.getQuestion().getCategory()))
                .map(r -> {
                    try {
                        int moodValue = Integer.parseInt(r.getAnswer());
                        return new DailyCheckinAnalyticsResponse.MoodTrend(
                                r.getResponseDate(),
                                moodValue,
                                r.getMetadata()
                        );
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<DailyCheckinAnalyticsResponse.CheckinSummary> getRecentCheckinsSummary(UUID userId) {
        LocalDate today = LocalDate.now();
        List<DailyCheckinAnalyticsResponse.CheckinSummary> summaries = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            boolean completed = responseRepository.existsByUserIdAndResponseDate(userId, date);
            int responseCount = responseRepository.findByUserIdAndResponseDateOrderByCreatedAtAsc(userId, date).size();

            summaries.add(new DailyCheckinAnalyticsResponse.CheckinSummary(
                    date, responseCount, completed
            ));
        }

        return summaries;
    }

    private double calculateAverageForCategory(String category, 
                                               List<DailyCheckinResponseEntity> responses) {
        if (responses.isEmpty()) {
            return 0.0;
        }

        double sum = 0.0;
        int count = 0;

        for (DailyCheckinResponseEntity response : responses) {
            try {
                double value = Double.parseDouble(response.getAnswer());
                sum += value;
                count++;
            } catch (NumberFormatException e) {
                // Skip non-numeric responses
            }
        }

        return count > 0 ? sum / count : 0.0;
    }

    private void checkAndCreateAlerts(UUID userId, List<DailyCheckinResponseEntity> responses) {
        // Implementation for creating alerts
        log.debug("Checking alerts for {} responses from user: {}", responses.size(), userId);
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private DailyCheckinResponse mapToDTO(DailyCheckinResponseEntity entity) {
        return DailyCheckinResponse.builder()
                .id(entity.getId())
                .questionId(entity.getQuestion().getId())
                .question(entity.getQuestion().getQuestion())
                .questionType(entity.getQuestion().getQuestionType())
                .category(entity.getQuestion().getCategory())
                .answer(entity.getAnswer())
                .responseDate(entity.getResponseDate())
                .metadata(entity.getMetadata())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private WellbeingAlertResponse mapAlertToDTO(WellbeingAlert alert) {
        return WellbeingAlertResponse.builder()
                .id(alert.getId())
                .level(alert.getLevel())
                .message(alert.getMessage())
                .suggestedAction(alert.getSuggestedAction())
                .resolved(alert.isResolved())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}