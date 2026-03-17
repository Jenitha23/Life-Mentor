package com.lifementor.service.impl;

import com.lifementor.dto.response.UserGoalResponse;
import com.lifementor.dto.response.WellbeingAlertResponse;
import com.lifementor.dto.response.WellbeingSummaryResponse;
import com.lifementor.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${app.frontend.reset-password-path:/reset-password}")
    private String resetPasswordPath;

    @Value("${app.application.name:Life Mentor}")
    private String applicationName;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Async
    public void sendResetPasswordEmail(String toEmail, String userName, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(applicationName + " - Password Reset Request");

            String resetLink = frontendUrl + resetPasswordPath + "?token=" + resetToken;

            String emailText = String.format(
                    "Dear %s,\n\n" +
                            "You have requested to reset your password for your %s account.\n\n" +
                            "Please click the link below to reset your password:\n" +
                            "%s\n\n" +
                            "This password reset link is valid for 1 hour.\n\n" +
                            "If you did not request a password reset, please ignore this email. " +
                            "Your password will remain unchanged.\n\n" +
                            "For security reasons, never share this link with anyone.\n\n" +
                            "Best regards,\n" +
                            "The %s Team",
                    userName,
                    applicationName,
                    resetLink,
                    applicationName
            );

            message.setText(emailText);

            mailSender.send(message);
            log.info("Password reset email successfully sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send password reset email: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendWelcomeEmail(String toEmail, String userName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to " + applicationName + "!");

            String emailText = String.format(
                    "Dear %s,\n\n" +
                            "Welcome to %s! We're thrilled to have you join our community.\n\n" +
                            "Your account has been successfully created and you can now start " +
                            "your journey towards a healthier and more balanced lifestyle.\n\n" +
                            "With %s, you'll receive personalized guidance and support " +
                            "to help you achieve your wellness goals.\n\n" +
                            "If you have any questions or need assistance, feel free to reach out.\n\n" +
                            "Best regards,\n" +
                            "The %s Team",
                    userName,
                    applicationName,
                    applicationName,
                    applicationName
            );

            message.setText(emailText);

            mailSender.send(message);
            log.info("Welcome email successfully sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage(), e);
            // Don't throw exception for welcome email - it's not critical
        }
    }

    @Override
    @Async
    public void sendPasswordChangedEmail(String toEmail, String userName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(applicationName + " - Password Changed Successfully");

            String emailText = String.format(
                    "Dear %s,\n\n" +
                            "This is to confirm that your %s account password has been successfully changed.\n\n" +
                            "If you did not make this change, please contact our support team immediately.\n\n" +
                            "For security:\n" +
                            "1. Never share your password with anyone\n" +
                            "2. Use a strong, unique password\n" +
                            "3. Enable two-factor authentication if available\n\n" +
                            "Best regards,\n" +
                            "The %s Team",
                    userName,
                    applicationName,
                    applicationName
            );

            message.setText(emailText);

            mailSender.send(message);
            log.info("Password changed notification email sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send password changed email to {}: {}", toEmail, e.getMessage(), e);
            // Don't throw exception - password change already happened
        }
    }

    @Override
    @Async
    public void sendDailyCheckinReminderEmail(String toEmail, String userName, List<String> questions) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(applicationName + " - Your Daily Wellbeing Check-in");

            StringBuilder questionList = new StringBuilder();
            for (int i = 0; i < questions.size(); i++) {
                questionList.append(i + 1).append(". ").append(questions.get(i)).append("\n");
            }

            String emailText = String.format(
                    "Dear %s,\n\n" +
                            "Here is your daily wellbeing check-in from %s.\n\n" +
                            "Take a moment to reflect on these questions:\n" +
                            "%s\n" +
                            "Small daily reflection can help you notice patterns and take better care of yourself.\n\n" +
                            "Best regards,\n" +
                            "The %s Team",
                    userName,
                    applicationName,
                    questionList,
                    applicationName
            );

            message.setText(emailText);
            mailSender.send(message);
            log.info("Daily check-in reminder email sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send daily check-in reminder email to {}: {}", toEmail, e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendWeeklyWellbeingSummaryEmail(String toEmail, String userName, WellbeingSummaryResponse summary) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(applicationName + " - Your Weekly Wellbeing Summary");
            message.setText(buildWeeklySummaryEmail(userName, summary));

            mailSender.send(message);
            log.info("Weekly wellbeing summary email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send weekly wellbeing summary email to {}: {}", toEmail, e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendWellbeingAlertEmail(String toEmail, String userName, List<WellbeingAlertResponse> alerts) {
        if (alerts == null || alerts.isEmpty()) {
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(applicationName + " - Wellbeing Alert");
            message.setText(buildWellbeingAlertEmail(userName, alerts));

            mailSender.send(message);
            log.info("Wellbeing alert email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send wellbeing alert email to {}: {}", toEmail, e.getMessage(), e);
        }
    }

    private String buildWeeklySummaryEmail(String userName, WellbeingSummaryResponse summary) {
        String activeGoalsText = formatActiveGoals(summary.getActiveGoals());
        String recommendationText = summary.getTodaysRecommendation() == null
                ? "Keep showing up for yourself this week."
                : summary.getTodaysRecommendation().getTitle() + ": " + summary.getTodaysRecommendation().getAction();

        return String.format(
                Locale.US,
                "Dear %s,\n\n" +
                        "Here is your weekly wellbeing summary from %s.\n\n" +
                        "Summary date: %s\n" +
                        "Current streak: %d day(s)\n" +
                        "Total check-in days: %d\n" +
                        "Average mood: %.1f\n" +
                        "Mood trend: %s\n" +
                        "Active alerts: %d\n\n" +
                        "Active goals:\n%s\n" +
                        "Recommended focus:\n%s\n\n" +
                        "Keep going. Small steps add up over time.\n\n" +
                        "Best regards,\n" +
                        "The %s Team",
                userName,
                applicationName,
                summary.getSummaryDate(),
                summary.getCurrentStreak(),
                summary.getTotalCheckins(),
                summary.getAverageMood(),
                summary.getMoodTrend(),
                summary.getActiveAlerts() == null ? 0 : summary.getActiveAlerts().size(),
                activeGoalsText,
                recommendationText,
                applicationName
        );
    }

    private String buildWellbeingAlertEmail(String userName, List<WellbeingAlertResponse> alerts) {
        StringBuilder alertText = new StringBuilder();
        for (int i = 0; i < alerts.size(); i++) {
            WellbeingAlertResponse alert = alerts.get(i);
            alertText.append(i + 1)
                    .append(". [")
                    .append(alert.getLevel())
                    .append("] ")
                    .append(alert.getMessage());

            if (alert.getSuggestedAction() != null && !alert.getSuggestedAction().isBlank()) {
                alertText.append("\n   Suggested action: ").append(alert.getSuggestedAction());
            }
            alertText.append("\n");
        }

        return String.format(
                "Dear %s,\n\n" +
                        "We noticed some wellbeing updates that may need your attention:\n\n" +
                        "%s\n" +
                        "Take a few minutes to review your wellbeing dashboard and check in with yourself.\n\n" +
                        "Best regards,\n" +
                        "The %s Team",
                userName,
                alertText,
                applicationName
        );
    }

    private String formatActiveGoals(List<UserGoalResponse> activeGoals) {
        if (activeGoals == null || activeGoals.isEmpty()) {
            return "- No active goals right now";
        }

        StringBuilder builder = new StringBuilder();
        activeGoals.stream()
                .limit(3)
                .forEach(goal -> builder.append("- ")
                        .append(goal.getGoalType())
                        .append(" (")
                        .append(goal.getProgressPercentage() == null ? 0 : goal.getProgressPercentage())
                        .append("% complete)\n"));
        return builder.toString();
    }
}
