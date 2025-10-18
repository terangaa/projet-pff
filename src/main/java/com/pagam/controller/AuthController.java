package com.pagam.controller;

import com.pagam.dto.RegisterRequest;
import com.pagam.entity.Utilisateur;
import com.pagam.service.AuthService;
import com.pagam.service.EmailService;
import com.pagam.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UtilisateurService utilisateurService;
    private final EmailService emailService;

    // -------------------- Inscription --------------------
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegisterRequest registerRequest, Model model) {
        try {
            authService.register(registerRequest);
            model.addAttribute("successMessage", "Compte créé avec succès ! Connectez-vous.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }

    // -------------------- Connexion --------------------
    @GetMapping("/login")
    public String showLoginForm(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) model.addAttribute("errorMessage", "Les identifications sont erronées.");
        if (logout != null) model.addAttribute("successMessage", "Vous vous êtes déconnecté avec succès.");

        return "login";
    }

    // -------------------- Mot de passe oublié --------------------
    @GetMapping("/oublier-password")
    public String forgotPasswordPage() {
        return "oublier-password";
    }

    @PostMapping("/oublier-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        Utilisateur utilisateur = utilisateurService.findByEmail(email);
        if (utilisateur == null) {
            model.addAttribute("errorMessage", "Aucun compte trouvé pour cet email !");
            return "oublier-password";
        }

        // Générer un token unique
        String token = UUID.randomUUID().toString();
        utilisateur.setResetToken(token);
        utilisateurService.save(utilisateur);

        // Construire le lien de réinitialisation
        String lien = "http://192.168.1.22:8086/auth/reset-password?token=" + token;
        String contenu = "<p>Bonjour " + utilisateur.getNom() + ",</p>"
                + "<p>Pour réinitialiser votre mot de passe, cliquez sur le lien ci-dessous :</p>"
                + "<a href='" + lien + "'>Réinitialiser le mot de passe</a>"
                + "<p>Si vous n'avez pas demandé de réinitialisation, ignorez ce mail.</p>";

        // Envoyer l'email
        emailService.envoyerAlerteEmail(email, "Réinitialisation mot de passe", contenu);

        model.addAttribute("successMessage", "Un lien de réinitialisation a été envoyé à " + email);
        return "oublier-password";
    }

    // -------------------- Réinitialisation mot de passe --------------------
    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {
        Utilisateur utilisateur = utilisateurService.findByResetToken(token);
        if (utilisateur == null) {
            model.addAttribute("errorMessage", "Token invalide !");
            return "login";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token,
                                       @RequestParam String newPassword,
                                       @RequestParam String confirmPassword,
                                       Model model) {
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Les mots de passe ne correspondent pas !");
            model.addAttribute("token", token);
            return "reset-password";
        }

        Utilisateur utilisateur = utilisateurService.findByResetToken(token);
        if (utilisateur == null) {
            model.addAttribute("errorMessage", "Token invalide !");
            return "reset-password";
        }

        utilisateurService.updatePassword(utilisateur, newPassword);
        model.addAttribute("successMessage", "Votre mot de passe a été réinitialisé avec succès !");
        return "login";
    }
}
