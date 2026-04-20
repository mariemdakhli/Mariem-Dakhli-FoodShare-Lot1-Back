package com.microservice.foodsharepi.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AuthResponse {
    private String token;
    private UserResponse user;
    // ✅ Ajouter l'utilisateur

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}