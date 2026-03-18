package com.lifementor.controller;

import com.lifementor.dto.response.ApiResponse;
import com.lifementor.dto.response.WellbeingAlertResponse;
import com.lifementor.dto.response.WellbeingSummaryResponse;
import com.lifementor.service.DailyCheckinService;
import com.lifementor.service.GoalService;
import com.lifementor.service.WellbeingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wellbeing")
public class WellbeingController {

    private static final Logger log = LoggerFactory.getLogger(WellbeingController.class);

    private final WellbeingService wellbeingService;
    private final DailyCheckinService checkinService;
    private final GoalService goalService;

    public WellbeingController(WellbeingService wellbeingService,
                               DailyCheckinService checkinService,
                               GoalService goalService) {
        this.wellbeingService = wellbeingService;
        this.checkinService = checkinService;
        this.goalService = goalService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse> getWellbeingSummary(@RequestAttribute("userId") UUID userId) {
        try {
            WellbeingSummaryResponse summary = wellbeingService.generateWellbeingSummary(userId);
            return ResponseEntity.ok(ApiResponse.success("Wellbeing summary retrieved successfully", summary));
        } catch (Exception e) {
            log.error("Failed to generate wellbeing summary: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/alerts")
    public ResponseEntity<ApiResponse> getActiveAlerts(@RequestAttribute("userId") UUID userId) {
        try {
            List<WellbeingAlertResponse> alerts = wellbeingService.getActiveAlerts(userId);
            return ResponseEntity.ok(ApiResponse.success("Active alerts retrieved successfully", alerts));
        } catch (Exception e) {
            log.error("Failed to retrieve active alerts: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/alerts/{alertId}/resolve")
    public ResponseEntity<ApiResponse> resolveAlert(
            @RequestAttribute("userId") UUID userId,
            @PathVariable("alertId") UUID alertId) {
        try {
            wellbeingService.resolveAlert(userId, alertId);
            return ResponseEntity.ok(ApiResponse.success("Alert resolved successfully"));
        } catch (Exception e) {
            log.error("Failed to resolve alert: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/trends")
    public ResponseEntity<ApiResponse> getWellbeingTrends(
            @RequestAttribute("userId") UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            var trends = wellbeingService.analyzeTrends(userId, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("Wellbeing trends retrieved successfully", trends));
        } catch (Exception e) {
            log.error("Failed to analyze wellbeing trends: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/recommendations")
    public ResponseEntity<ApiResponse> getDailyRecommendations(@RequestAttribute("userId") UUID userId) {
        try {
            var recommendations = wellbeingService.generateDailyRecommendations(userId);
            return ResponseEntity.ok(ApiResponse.success("Daily recommendations retrieved successfully", recommendations));
        } catch (Exception e) {
            log.error("Failed to generate daily recommendations: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}