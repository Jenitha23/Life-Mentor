package com.lifementor.controller;

import com.lifementor.dto.request.DailyCheckinBatchRequest;
import com.lifementor.dto.request.DailyCheckinRequest;
import com.lifementor.dto.response.ApiResponse;
import com.lifementor.dto.response.DailyCheckinAnalyticsResponse;
import com.lifementor.dto.response.DailyCheckinResponse;
import com.lifementor.dto.response.WellbeingAlertResponse;
import com.lifementor.service.DailyCheckinService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/daily-checkin")
public class DailyCheckinController {

    private static final Logger log = LoggerFactory.getLogger(DailyCheckinController.class);

    private final DailyCheckinService checkinService;

    public DailyCheckinController(DailyCheckinService checkinService) {
        this.checkinService = checkinService;
    }

    @PostMapping("/batch")
    public ResponseEntity<ApiResponse> submitDailyCheckin(
            @RequestAttribute("userId") UUID userId,
            @Valid @RequestBody DailyCheckinBatchRequest request) {
        try {
            List<DailyCheckinResponse> responses = checkinService.submitDailyCheckin(userId, request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Daily check-in submitted successfully", responses));
        } catch (Exception e) {
            log.error("Failed to submit daily check-in: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/single")
    public ResponseEntity<ApiResponse> submitSingleResponse(
            @RequestAttribute("userId") UUID userId,
            @Valid @RequestBody DailyCheckinRequest request) {
        try {
            DailyCheckinResponse response = checkinService.submitSingleResponse(userId, request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Response submitted successfully", response));
        } catch (Exception e) {
            log.error("Failed to submit response: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse> getTodaysCheckin(@RequestAttribute("userId") UUID userId) {
        try {
            List<DailyCheckinResponse> responses = checkinService.getTodaysCheckin(userId);
            return ResponseEntity.ok(ApiResponse.success("Today's check-in retrieved successfully", responses));
        } catch (Exception e) {
            log.error("Failed to retrieve today's check-in: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<ApiResponse> getCheckinByDate(
            @RequestAttribute("userId") UUID userId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<DailyCheckinResponse> responses = checkinService.getCheckinByDate(userId, date);
            return ResponseEntity.ok(ApiResponse.success("Check-in retrieved successfully", responses));
        } catch (Exception e) {
            log.error("Failed to retrieve check-in by date: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse> getAnalytics(
            @RequestAttribute("userId") UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            DailyCheckinAnalyticsResponse analytics = checkinService.getAnalytics(userId, startDate, endDate);
            return ResponseEntity.ok(ApiResponse.success("Analytics retrieved successfully", analytics));
        } catch (Exception e) {
            log.error("Failed to retrieve analytics: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/alerts")
    public ResponseEntity<ApiResponse> checkAlerts(@RequestAttribute("userId") UUID userId) {
        try {
            List<WellbeingAlertResponse> alerts = checkinService.checkWellbeingAlerts(userId);
            return ResponseEntity.ok(ApiResponse.success("Alerts checked successfully", alerts));
        } catch (Exception e) {
            log.error("Failed to check alerts: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/streak")
    public ResponseEntity<ApiResponse> getStreak(@RequestAttribute("userId") UUID userId) {
        try {
            int streak = checkinService.getCurrentStreak(userId);
            return ResponseEntity.ok(ApiResponse.success("Streak retrieved successfully", 
                    new StreakResponse(streak, checkinService.hasCompletedTodayCheckin(userId))));
        } catch (Exception e) {
            log.error("Failed to retrieve streak: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/responses/{responseId}")
    public ResponseEntity<ApiResponse> deleteResponse(
            @RequestAttribute("userId") UUID userId,
            @PathVariable UUID responseId) {
        try {
            checkinService.deleteCheckinResponse(userId, responseId);
            return ResponseEntity.ok(ApiResponse.success("Response deleted successfully"));
        } catch (Exception e) {
            log.error("Failed to delete response: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Inner class for streak response
    private static class StreakResponse {
        private int currentStreak;
        private boolean completedToday;

        public StreakResponse(int currentStreak, boolean completedToday) {
            this.currentStreak = currentStreak;
            this.completedToday = completedToday;
        }

        public int getCurrentStreak() { return currentStreak; }
        public boolean isCompletedToday() { return completedToday; }
    }
}