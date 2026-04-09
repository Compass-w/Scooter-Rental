package com.scooterrental.backend.dto.auth;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String email;
}
