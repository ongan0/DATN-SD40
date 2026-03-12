package com.example.datn.service;

import com.example.datn.dto.auth.*;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse loginAdmin(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    void logout(String userId);
    void register(RegisterRequest request);
    void changePassword(String userId, ChangePasswordRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void verifyOtp(VerifyOtpRequest request);
}
