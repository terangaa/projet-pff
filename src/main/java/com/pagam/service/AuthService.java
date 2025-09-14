package com.pagam.service;

import com.pagam.dto.AuthRequest;
import com.pagam.dto.AuthResponse;
import com.pagam.dto.RegisterRequest;
import com.pagam.entity.Role;
import com.pagam.entity.Utilisateur;
import com.pagam.repository.UtilisateurRepository;
import com.pagam.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Inscription d’un utilisateur
     */
    public AuthResponse register(RegisterRequest registerRequest) {
        // Vérifie si l’email existe déjà
        if (utilisateurRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé !");
        }

        // Crée un nouvel utilisateur
        Utilisateur user = Utilisateur.builder()
                .nom(registerRequest.getNom())
                .email(registerRequest.getEmail())
                .motDePasse(passwordEncoder.encode(registerRequest.getMotDePasse()))
                .role(registerRequest.getRole() != null ? registerRequest.getRole() : Role.VISITEUR)
                .build();

        utilisateurRepository.save(user);

        // Génère un JWT
        String token = jwtService.generateToken(user);

        // Retourne AuthResponse complet avec rôle et expiration
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .roles(List.of(user.getRole().name()))
                .expiresAt(System.currentTimeMillis() + jwtService.getExpiration()) // timestamp expiration
                .build();
    }

    /**
     * Connexion d’un utilisateur
     */
    public AuthResponse login(AuthRequest authRequest) {
        try {
            // Vérifie les identifiants
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getMotDePasse())
            );

            // Charge l’utilisateur
            Utilisateur user = utilisateurRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Génère un JWT
            String token = jwtService.generateToken(user);

            // Retourne AuthResponse complet
            return AuthResponse.builder()
                    .token(token)
                    .email(user.getEmail())
                    .roles(List.of(user.getRole().name()))
                    .expiresAt(System.currentTimeMillis() + jwtService.getExpiration())
                    .build();

        } catch (AuthenticationException e) {
            throw new RuntimeException("Identifiants invalides !");
        }
    }
}
