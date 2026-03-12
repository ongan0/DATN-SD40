package com.example.datn.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
}
