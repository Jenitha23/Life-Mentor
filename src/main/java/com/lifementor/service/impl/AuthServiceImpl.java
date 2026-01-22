package com.lifementor.service.impl;

import com.lifementor.dto.request.*;
import com.lifementor.dto.response.AuthResponse;
import com.lifementor.entity.User;
import com.lifementor.exception.*;
import com.lifementor.repository.UserRepository;
import com.lifementor.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final ProfileService profileService;


    public AuthServiceImpl(UserRepository userRepository, PasswordService passwordService,
                           TokenService tokenService, EmailService emailService, ProfileService profileService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
        this.emailService = emailService;
        this.profileService = profileService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        // Validate password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException("Passwords do not match");
        }

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        // Validate password strength
        if (!passwordService.validatePasswordStrength(request.getPassword())) {
            throw new ValidationException("Password does not meet security requirements");
        }

        // Create new user
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordService.hashPassword(request.getPassword()))
                .emailVerified(false)
                .failedLoginAttempts(0)
                .accountLocked(false)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());

        // Send welcome email
        try {
            emailService.sendWelcomeEmail(user.getEmail(), user.getName());
        } catch (Exception e) {
            log.warn("Failed to send welcome email to {}: {}", user.getEmail(), e.getMessage());
            // Continue even if email fails
        }

        // Generate token
        String token = tokenService.generateToken(user);

        return buildAuthResponse(token, user, "Registration successful");
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            // For security, don't reveal if user exists
            log.info("Password reset requested for email (not found in system): {}", request.getEmail());
            return;
        }

        User user = userOptional.get();

        // Generate reset token
        String resetToken = tokenService.generateResetToken();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));

        userRepository.save(user);

        log.info("Password reset token generated for user: {}", user.getEmail());

        // Send password reset email
        try {
            emailService.sendResetPasswordEmail(user.getEmail(), user.getName(), resetToken);
            log.info("Password reset email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", user.getEmail(), e.getMessage());
            throw new RuntimeException("Failed to send password reset email. Please try again later.");
        }
    }

    @Override
    public AuthResponse resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException("Passwords do not match");
        }

        Optional<User> userOptional = userRepository.findByResetToken(request.getToken());
        if (userOptional.isEmpty()) {
            throw new InvalidTokenException("Invalid or expired reset token");
        }

        User user = userOptional.get();

        // Check if token is expired
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            userRepository.save(user);
            throw new InvalidTokenException("Reset token has expired");
        }

        // Validate new password strength
        if (!passwordService.validatePasswordStrength(request.getNewPassword())) {
            throw new ValidationException("Password does not meet security requirements");
        }

        // Update password
        user.setPassword(passwordService.hashPassword(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        user.resetFailedLoginAttempts(); // Reset lock if any

        user = userRepository.save(user);

        // Send password changed notification email
        try {
            emailService.sendPasswordChangedEmail(user.getEmail(), user.getName());
        } catch (Exception e) {
            log.warn("Failed to send password changed email to {}: {}", user.getEmail(), e.getMessage());
            // Continue even if email fails
        }

        // Generate new login token
        String token = tokenService.generateToken(user);
        log.info("Password reset successful for user: {}", user.getEmail());

        return buildAuthResponse(token, user, "Password reset successful");
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            throw new AuthenticationException("Invalid credentials");
        }

        User user = userOptional.get();

        // Check if account is locked
        if (user.isAccountCurrentlyLocked()) {
            throw new AccountLockedException("Account is locked. Please try again later or reset password");
        }

        // Verify password
        if (!passwordService.verifyPassword(request.getPassword(), user.getPassword())) {
            user.incrementFailedLoginAttempts();
            userRepository.save(user);
            throw new AuthenticationException("Invalid credentials");
        }

        // Reset failed attempts on successful login
        user.resetFailedLoginAttempts();
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Generate token
        String token = tokenService.generateToken(user);
        log.info("User logged in successfully: {}", user.getEmail());

        return buildAuthResponse(token, user, "Login successful");
    }

    @Override
    public void logout(String token) {
        log.info("User logged out");
    }

    @Override
    public User getCurrentUser() {
        throw new UnsupportedOperationException("To be implemented with Spring Security");
    }

    @Override
    public void validateToken(String token) {
        if (!tokenService.validateToken(token)) {
            throw new InvalidTokenException("Invalid or expired token");
        }
    }

    private AuthResponse buildAuthResponse(String token, User user, String message) {
        AuthResponse.UserResponse userResponse = AuthResponse.UserResponse.builder()
                .id(user.getId().toString())
                .name(user.getName())
                .email(user.getEmail())
                .emailVerified(user.isEmailVerified())
                .build();

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .message(message)
                .user(userResponse)
                .build();
    }
}