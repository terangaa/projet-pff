package com.pagam.controller;

import com.pagam.entity.Utilisateur;
import com.pagam.service.UtilisateurService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/parametres")
public class ParametreController {

    private final UtilisateurService utilisateurService;
    private final PasswordEncoder passwordEncoder;

    public ParametreController(UtilisateurService utilisateurService, PasswordEncoder passwordEncoder) {
        this.utilisateurService = utilisateurService;
        this.passwordEncoder = passwordEncoder;
    }

    // Page profil
    @GetMapping("/profil")
    public String profil(Model model, Authentication authentication) {
        // Récupération de l'utilisateur connecté
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Utilisateur utilisateur = utilisateurService.findByEmail(userDetails.getUsername());

        // Ajout de l'utilisateur au modèle pour la page profil
        model.addAttribute("utilisateur", utilisateur);
        return "parametres/parametre-profil"; // Nom de la page JSP ou HTML
    }


    // Page modification profil
    @GetMapping("/modifier-profil")
    public String modifierProfil(Model model, Authentication authentication) {
        Utilisateur utilisateur = getUtilisateurConnecte(authentication);
        model.addAttribute("utilisateur", utilisateur);
        return "parametres/modifier-profil";
    }

    // Enregistrement modifications du profil
    @PostMapping("/modifier-profil")
    public String enregistrerProfil(@RequestParam String nom,
                                    @RequestParam String email,
                                    @RequestParam(required=false) String localite,
                                    Principal principal,
                                    Model model) {
        Utilisateur utilisateur = getUtilisateurConnecte(principal);
        utilisateur.setNom(nom);
        utilisateur.setEmail(email);
        utilisateur.setLocalite(localite); // <-- mise à jour
        utilisateurService.save(utilisateur);

        model.addAttribute("utilisateur", utilisateur);
        model.addAttribute("success", "Profil mis à jour avec succès !");
        return "parametres/modifier-profil";
    }


    // Page changer mot de passe
    @GetMapping("/modifier-mot-de-passe")
    public String changerMotDePasse(Model model, @AuthenticationPrincipal Utilisateur utilisateur) {
        model.addAttribute("utilisateur", utilisateur);
        return "parametres/modifier-mot-de-passe"; // nom du fichier changer-mot-de-passe.html
    }

    @PostMapping("/modifier-mot-de-passe")
    public String modifierMotDePasse(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        // 1️⃣ Récupération de l'utilisateur connecté
        Utilisateur utilisateur = utilisateurService.findByEmail(principal.getName());
        if (utilisateur == null) {
            redirectAttributes.addFlashAttribute("error", "Utilisateur introuvable !");
            return "redirect:/login";
        }

        // 2️⃣ Vérification du mot de passe actuel
        if (!passwordEncoder.matches(currentPassword, utilisateur.getMotDePasse())) {
            redirectAttributes.addFlashAttribute("error", "Le mot de passe actuel est incorrect !");
            return "redirect:/parametres/modifier-mot-de-passe";
        }

        // 3️⃣ Vérification que les nouveaux mots de passe correspondent
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Les nouveaux mots de passe ne correspondent pas !");
            return "redirect:/parametres/modifier-mot-de-passe";
        }

        // 4️⃣ Mise à jour du mot de passe
        utilisateur.setMotDePasse(passwordEncoder.encode(newPassword));
        utilisateurService.save(utilisateur);

        // 5️⃣ Message de confirmation
        redirectAttributes.addFlashAttribute("success", "Mot de passe modifié avec succès !");
        return "redirect:/parametres/profil";
    }

    // Page sécurité (changement mot de passe à implémenter)
    @GetMapping("/securite")
    public String securite() {
        return "parametres/parametre-securite";
    }

    // Méthode utilitaire pour récupérer l'utilisateur connecté via Authentication
    private Utilisateur getUtilisateurConnecte(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof UserDetails)) {
            throw new IllegalStateException("Utilisateur non authentifié");
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return utilisateurService.findByEmail(userDetails.getUsername());
    }

    // Méthode utilitaire pour récupérer l'utilisateur connecté via Principal
    private Utilisateur getUtilisateurConnecte(Principal principal) {
        return utilisateurService.findByEmail(principal.getName());
    }

    @GetMapping("/about")
    public String aPropos() {
        return "homes/about"; // ou "parametres/a-propos" selon ton template
    }

}
