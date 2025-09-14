package com.pagam.controller;

import com.pagam.dto.RegisterRequest;
import com.pagam.service.AuthService;
import com.pagam.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UtilisateurService utilisateurService;

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

    // -------------------- Login --------------------
    @GetMapping("/login")
    public String showLoginForm(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) model.addAttribute("errorMessage", "Les identifications sont erronées.");
        if (logout != null) model.addAttribute("successMessage", "Vous vous êtes déconnecté avec succès.");

        return "login";
    }

    @GetMapping("/oublier-password")
    public String forgotPasswordPage() {
        return "oublier-password"; // correspond au fichier forgot-password.html
    }

    @PostMapping("/oublier-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        // 👉 logique pour générer un token et envoyer un mail
        model.addAttribute("successMessage", "Un lien de réinitialisation a été envoyé à " + email);
        return "oublier-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam String token, Model model) {
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

        // 👉 logique pour valider le token et mettre à jour le mot de passe
        model.addAttribute("successMessage", "Votre mot de passe a été réinitialisé avec succès !");
        return "login";
    }

    // Spring Security gère la POST /auth/login automatiquement
}
