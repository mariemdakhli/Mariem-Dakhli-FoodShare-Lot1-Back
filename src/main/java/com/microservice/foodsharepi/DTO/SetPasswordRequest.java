package com.microservice.foodsharepi.DTO;

import lombok.Data;

@Data
public class SetPasswordRequest {
    private String token;
    private String password;
    private String confirmPassword;
}