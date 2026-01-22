package com.lifementor.service.impl;

import com.lifementor.dto.request.PasswordChangeRequest;
import com.lifementor.dto.request.ProfileUpdateRequest;
import com.lifementor.dto.response.ProfileResponse;
import com.lifementor.entity.User;
import com.lifementor.exception.DuplicateResourceException;
import com.lifementor.exception.ResourceNotFoundException;
import com.lifementor.exception.ValidationException;
import com.lifementor.repository.UserRepository;
import com.lifementor.service.EmailService;
import com.lifementor.service.PasswordService;
import com.lifementor.service.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ProfileServiceImpl implements ProfileService {

    private static final Logger log = LoggerFactory.getLogger(ProfileServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final EmailService emailService;

    @Value("${app.upload.profile-pictures-dir:uploads/profile-pictures}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.static-files.path:/files}")
    private String staticFilesPath;

    public ProfileServiceImpl(UserRepository userRepository, PasswordService passwordService,
                              EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.emailService = emailService;
    }

    @Override
    public ProfileResponse getProfile(UUID userId) {
        User user = getUserById(userId);
        return mapToProfileResponse(user);
    }

    @Override
    public ProfileResponse updateProfile(UUID userId, ProfileUpdateRequest request) {
        User user = getUserById(userId);

        // Check if email is being changed and if it's already taken
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
                throw new DuplicateResourceException("Email is already registered");
            }

            // Update email and mark as unverified
            user.setEmail(request.getEmail());
            user.setEmailVerified(false);
            log.info("Email updated for user: {}, verification required", userId);
        }

        // Update other fields
        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
        }

        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }

        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }

        user = userRepository.save(user);
        log.info("Profile updated for user: {}", userId);

        return mapToProfileResponse(user);
    }

    @Override
    public void changePassword(UUID userId, PasswordChangeRequest request) {
        User user = getUserById(userId);

        // Validate current password
        if (!passwordService.verifyPassword(request.getCurrentPassword(), user.getPassword())) {
            throw new ValidationException("Current password is incorrect");
        }

        // Validate new password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException("New passwords do not match");
        }

        if (!passwordService.validatePasswordStrength(request.getNewPassword())) {
            throw new ValidationException("New password does not meet security requirements");
        }

        // Check if new password is same as old password
        if (passwordService.verifyPassword(request.getNewPassword(), user.getPassword())) {
            throw new ValidationException("New password cannot be the same as current password");
        }

        // Update password
        user.setPassword(passwordService.hashPassword(request.getNewPassword()));
        userRepository.save(user);

        // Send password changed notification
        try {
            emailService.sendPasswordChangedEmail(user.getEmail(), user.getName());
        } catch (Exception e) {
            log.warn("Failed to send password changed email: {}", e.getMessage());
        }

        log.info("Password changed for user: {}", userId);
    }

    @Override
    public String uploadProfilePicture(UUID userId, MultipartFile file) throws IOException {
        User user = getUserById(userId);

        // Validate file
        if (file.isEmpty()) {
            throw new ValidationException("File is empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new ValidationException("Invalid file name");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ValidationException("Only image files are allowed");
        }

        // Validate file size (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new ValidationException("File size must be less than 5MB");
        }

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // Save file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath);

        // Delete old profile picture if exists
        if (user.getProfilePictureUrl() != null) {
            deleteOldProfilePicture(user.getProfilePictureUrl());
        }

        // Update user with new profile picture URL
        String profilePictureUrl = baseUrl + staticFilesPath + "/profile-pictures/" + uniqueFilename;
        user.setProfilePictureUrl(profilePictureUrl);
        userRepository.save(user);

        log.info("Profile picture uploaded for user: {}", userId);

        return profilePictureUrl;
    }

    @Override
    public void deleteProfilePicture(UUID userId) {
        User user = getUserById(userId);

        if (user.getProfilePictureUrl() != null) {
            deleteOldProfilePicture(user.getProfilePictureUrl());
            user.setProfilePictureUrl(null);
            userRepository.save(user);
            log.info("Profile picture deleted for user: {}", userId);
        }
    }

    @Override
    public void deleteAccount(UUID userId) {
        User user = getUserById(userId);

        // Delete profile picture if exists
        if (user.getProfilePictureUrl() != null) {
            deleteOldProfilePicture(user.getProfilePictureUrl());
        }

        userRepository.delete(user);
        log.info("Account deleted for user: {}", userId);
    }

    @Override
    public ProfileResponse deactivateAccount(UUID userId) {
        User user = getUserById(userId);
        log.info("Account deactivation requested for user: {}", userId);
        return mapToProfileResponse(user);
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private ProfileResponse mapToProfileResponse(User user) {
        return ProfileResponse.builder()
                .id(user.getId().toString())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .bio(user.getBio())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .profilePictureUrl(user.getProfilePictureUrl())
                .emailVerified(user.isEmailVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }

    private void deleteOldProfilePicture(String profilePictureUrl) {
        try {
            // Extract filename from URL
            String filename = profilePictureUrl.substring(profilePictureUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDir).resolve(filename);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            log.error("Failed to delete old profile picture: {}", e.getMessage());
        }
    }
}