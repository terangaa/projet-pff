package com.pagam.dto;

import com.pagam.entity.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String nom;
    private String email;
    private String motDePasse;
    private Role role;
}
