package com.lifementor.service;

import com.lifementor.dto.request.PasswordChangeRequest;
import com.lifementor.dto.request.ProfileUpdateRequest;
import com.lifementor.dto.response.ProfileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface ProfileService {
    ProfileResponse getProfile(UUID userId);
    ProfileResponse updateProfile(UUID userId, ProfileUpdateRequest request);
    void changePassword(UUID userId, PasswordChangeRequest request);
    String uploadProfilePicture(UUID userId, MultipartFile file) throws IOException;
    void deleteProfilePicture(UUID userId);
    void deleteAccount(UUID userId);
    ProfileResponse deactivateAccount(UUID userId);
}
