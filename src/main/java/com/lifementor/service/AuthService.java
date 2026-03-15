package com.lifementor.service;

import com.lifementor.dto.request.*;
import com.lifementor.dto.response.AuthResponse;
import com.lifementor.entity.User;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void logout(String token);
    void forgotPassword(ForgotPasswordRequest request);
    AuthResponse resetPassword(ResetPasswordRequest request);
    User getCurrentUser();
    void validateToken(String token);
}