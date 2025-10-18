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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    // -------------------- Inscription --------------------
    public AuthResponse register(RegisterRequest registerRequest) {
        if (utilisateurRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé !");
        }

        Utilisateur user = Utilisateur.builder()
                .nom(registerRequest.getNom())
                .email(registerRequest.getEmail())
                .motDePasse(passwordEncoder.encode(registerRequest.getMotDePasse()))
                .role(registerRequest.getRole() != null ? registerRequest.getRole() : Role.VISITEUR)
                .build();

        utilisateurRepository.save(user);

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .roles(List.of(user.getRole().name()))
                .expiresAt(System.currentTimeMillis() + jwtService.getExpiration())
                .build();
    }

    // -------------------- Connexion --------------------
    public AuthResponse login(AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getMotDePasse())
            );

            Utilisateur user = utilisateurRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            String token = jwtService.generateToken(user);

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

    // -------------------- Mot de passe oublié --------------------
    public void sendResetPasswordEmail(String email) {
        Optional<Utilisateur> userOpt = utilisateurRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            Utilisateur user = userOpt.get();
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            utilisateurRepository.save(user);

            String lien = "http://localhost:8080/auth/reset-password?token=" + token;

            String contenu = "<p>Pour réinitialiser votre mot de passe, cliquez sur ce lien :</p>" +
                    "<a href='" + lien + "'>Réinitialiser le mot de passe</a>";

            emailService.envoyerAlerteEmail(email, "Réinitialisation mot de passe", contenu);
        } else {
            throw new RuntimeException("Aucun utilisateur trouvé avec cet email !");
        }
    }

    // -------------------- Validation token --------------------
    public Utilisateur validateResetToken(String token) {
        return utilisateurRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide ou expiré !"));
    }

    // -------------------- Réinitialisation mot de passe --------------------
    public void resetPassword(String token, String newPassword) {
        Utilisateur user = validateResetToken(token);
        user.setMotDePasse(passwordEncoder.encode(newPassword));
        user.setResetToken(null); // Supprime le token après usage
        utilisateurRepository.save(user);
    }
}
