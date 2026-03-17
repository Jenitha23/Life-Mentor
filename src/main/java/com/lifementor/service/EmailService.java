package com.lifementor.service;

import com.lifementor.dto.response.WellbeingAlertResponse;
import com.lifementor.dto.response.WellbeingSummaryResponse;

import java.util.List;

public interface EmailService {
    void sendResetPasswordEmail(String toEmail, String userName, String resetToken);
    void sendWelcomeEmail(String toEmail, String userName);
    void sendPasswordChangedEmail(String toEmail, String userName);
    void sendDailyCheckinReminderEmail(String toEmail, String userName, List<String> questions);
    void sendWeeklyWellbeingSummaryEmail(String toEmail, String userName, WellbeingSummaryResponse summary);
    void sendWellbeingAlertEmail(String toEmail, String userName, List<WellbeingAlertResponse> alerts);
}
