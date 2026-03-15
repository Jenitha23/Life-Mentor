package com.lifementor.service;

import com.lifementor.dto.response.WellbeingAlertResponse;
import com.lifementor.dto.response.WellbeingSummaryResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface WellbeingService {

    WellbeingSummaryResponse generateWellbeingSummary(UUID userId);

    List<WellbeingAlertResponse> getActiveAlerts(UUID userId);

    void resolveAlert(UUID userId, UUID alertId);

    Map<String, Object> analyzeTrends(UUID userId, LocalDate startDate, LocalDate endDate);

    List<Map<String, Object>> generateDailyRecommendations(UUID userId);
}