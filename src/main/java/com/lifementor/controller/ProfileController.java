package com.lifementor.controller;

import com.lifementor.dto.request.PasswordChangeRequest;
import com.lifementor.dto.request.ProfileUpdateRequest;
import com.lifementor.dto.response.ApiResponse;
import com.lifementor.dto.response.ProfileResponse;
import com.lifementor.service.ProfileService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getProfile(@RequestAttribute("userId") UUID userId) {
        try {
            ProfileResponse profile = profileService.getProfile(userId);
            return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", profile));
        } catch (Exception e) {
            log.error("Failed to get profile: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve profile"));
        }
    }

    @PutMapping
    public ResponseEntity<ApiResponse> updateProfile(
            @RequestAttribute("userId") UUID userId,
            @Valid @RequestBody ProfileUpdateRequest request) {
        try {
            ProfileResponse updatedProfile = profileService.updateProfile(userId, request);
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedProfile));
        } catch (Exception e) {
            log.error("Failed to update profile: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(
            @RequestAttribute("userId") UUID userId,
            @Valid @RequestBody PasswordChangeRequest request) {
        try {
            profileService.changePassword(userId, request);
            return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
        } catch (Exception e) {
            log.error("Failed to change password: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/upload-picture")
    public ResponseEntity<ApiResponse> uploadProfilePicture(
            @RequestAttribute("userId") UUID userId,
            @RequestParam("file") MultipartFile file) {
        try {
            String profilePictureUrl = profileService.uploadProfilePicture(userId, file);
            return ResponseEntity.ok(ApiResponse.success("Profile picture uploaded successfully", profilePictureUrl));
        } catch (IOException e) {
            log.error("Failed to upload profile picture: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to upload profile picture"));
        } catch (Exception e) {
            log.error("Failed to upload profile picture: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/picture")
    public ResponseEntity<ApiResponse> deleteProfilePicture(@RequestAttribute("userId") UUID userId) {
        try {
            profileService.deleteProfilePicture(userId);
            return ResponseEntity.ok(ApiResponse.success("Profile picture deleted successfully"));
        } catch (Exception e) {
            log.error("Failed to delete profile picture: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete profile picture"));
        }
    }

    @DeleteMapping("/account")
    public ResponseEntity<ApiResponse> deleteAccount(@RequestAttribute("userId") UUID userId) {
        try {
            profileService.deleteAccount(userId);
            return ResponseEntity.ok(ApiResponse.success("Account deleted successfully"));
        } catch (Exception e) {
            log.error("Failed to delete account: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete account"));
        }
    }

    @PostMapping("/deactivate")
    public ResponseEntity<ApiResponse> deactivateAccount(@RequestAttribute("userId") UUID userId) {
        try {
            ProfileResponse profile = profileService.deactivateAccount(userId);
            return ResponseEntity.ok(ApiResponse.success("Account deactivated successfully", profile));
        } catch (Exception e) {
            log.error("Failed to deactivate account: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to deactivate account"));
        }
    }
}
