package com.example.datn.controller;

import com.example.datn.core.common.base.ResponseObject;
import com.example.datn.dto.auth.*;
import com.example.datn.infrastructure.config.global.GlobalVariables;
import com.example.datn.infrastructure.constant.GlobalVariablesConstant;
import com.example.datn.infrastructure.constant.MappingConstants;
import com.example.datn.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(MappingConstants.API_AUTH_PREFIX)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final GlobalVariables globalVariables;

    /**
     * Đăng nhập dành cho Khách hàng
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ResponseObject.success(response, "Đăng nhập thành công"));
    }

    /**
     * Đăng nhập dành cho Admin / Nhân viên
     * POST /api/v1/auth/login-admin
     */
    @PostMapping("/login-admin")
    public ResponseEntity<?> loginAdmin(@RequestBody LoginRequest request) {
        AuthResponse response = authService.loginAdmin(request);
        return ResponseEntity.ok(ResponseObject.success(response, "Đăng nhập thành công"));
    }

    /**
     * Đăng ký tài khoản Khách hàng
     * POST /api/v1/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(ResponseObject.success("Đăng ký thành công"));
    }

    /**
     * Cấp lại access token từ refresh token
     * POST /api/v1/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ResponseObject.success(response, "Làm mới token thành công"));
    }

    /**
     * Đăng xuất — revoke refresh token
     * POST /api/v1/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        String userId = (String) globalVariables.getGlobalVariable(GlobalVariablesConstant.CURRENT_USER_ID);
        authService.logout(userId);
        return ResponseEntity.ok(ResponseObject.success("Đăng xuất thành công"));
    }

    /**
     * Đổi mật khẩu
     * POST /api/v1/auth/change-password
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        String userId = (String) globalVariables.getGlobalVariable(GlobalVariablesConstant.CURRENT_USER_ID);
        authService.changePassword(userId, request);
        return ResponseEntity.ok(ResponseObject.success("Đổi mật khẩu thành công"));
    }

    /**
     * Gửi OTP về email để quên mật khẩu
     * POST /api/v1/auth/forgot-password
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ResponseObject.success("OTP đã được gửi đến email của bạn"));
    }

    /**
     * Xác thực OTP và đặt lại mật khẩu mới
     * POST /api/v1/auth/verify-otp
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest request) {
        authService.verifyOtp(request);
        return ResponseEntity.ok(ResponseObject.success("Đặt lại mật khẩu thành công"));
    }
}
