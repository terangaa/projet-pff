package com.pagam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String email;
    private List<String> roles;
    private long expiresAt; // timestamp en millisecondes

    public AuthResponse(String token) {
        this.token = token;
    }
}
