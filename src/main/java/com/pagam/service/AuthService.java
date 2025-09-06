package com.pagam.service;

import com.pagam.dto.AuthRequest;
import com.pagam.dto.AuthResponse;
import com.pagam.dto.RegisterRequest;
import com.pagam.entity.Utilisateur;
import com.pagam.repository.UtilisateurRepository;
import com.pagam.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository userRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest req) {
        Utilisateur user = Utilisateur.builder()
                .nom(req.getNom())
                .email(req.getEmail())
                .motDePasse(encoder.encode(req.getMotDePasse()))
                .role(req.getRole())
                .build();
        userRepo.save(user);
        return new AuthResponse(jwtService.generateToken(user.getEmail()));
    }

    public AuthResponse login(AuthRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(),
                        req.getPassword()
                )
        );
        return new AuthResponse(jwtService.generateToken(req.getEmail()));
    }
}
