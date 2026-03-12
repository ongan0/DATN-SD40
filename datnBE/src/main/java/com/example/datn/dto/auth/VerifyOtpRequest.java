package com.example.datn.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOtpRequest {
    private String email;
    private String otpCode;
    private String newPassword;
}
