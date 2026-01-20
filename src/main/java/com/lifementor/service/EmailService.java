// src/main/java/com/lifementor/service/EmailService.java
package com.lifementor.service;

public interface EmailService {
    void sendResetPasswordEmail(String toEmail, String userName, String resetToken);
    void sendWelcomeEmail(String toEmail, String userName);
    void sendPasswordChangedEmail(String toEmail, String userName);
}