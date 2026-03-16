package com.lifementor.service;

import com.lifementor.dto.request.DailyCheckinBatchRequest;
import com.lifementor.dto.request.DailyCheckinRequest;
import com.lifementor.dto.response.DailyCheckinAnalyticsResponse;
import com.lifementor.dto.response.DailyCheckinQuestionResponse;
import com.lifementor.dto.response.DailyCheckinResponse;
import com.lifementor.dto.response.WellbeingAlertResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DailyCheckinService {

    List<DailyCheckinResponse> submitDailyCheckin(UUID userId, DailyCheckinBatchRequest request);

    DailyCheckinResponse submitSingleResponse(UUID userId, DailyCheckinRequest request);

    List<DailyCheckinResponse> getTodaysCheckin(UUID userId);

    List<DailyCheckinQuestionResponse> getActiveQuestions();

    List<DailyCheckinQuestionResponse> getActiveQuestionsByCategory(String category);

    List<DailyCheckinResponse> getCheckinByDate(UUID userId, LocalDate date);

    DailyCheckinAnalyticsResponse getAnalytics(UUID userId, LocalDate startDate, LocalDate endDate);

    List<WellbeingAlertResponse> checkWellbeingAlerts(UUID userId);

    boolean hasCompletedTodayCheckin(UUID userId);

    int getCurrentStreak(UUID userId);

    void deleteCheckinResponse(UUID userId, UUID responseId);
}
