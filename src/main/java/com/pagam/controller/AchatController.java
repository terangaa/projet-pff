package com.pagam.controller;

import com.pagam.entity.Commande;
import com.pagam.service.PanierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AchatController {

    private final PanierService panierService;

    @GetMapping("/achats/historique")
    public String afficherCommandesUtilisateur(Model model) {
        try {
            String email = panierService.getEmailUtilisateurConnecte();
            List<Commande> commandes = panierService.listerCommandesUtilisateur(email);
            model.addAttribute("commandes", commandes);
            model.addAttribute("email", email);
        } catch (Exception e) {
            model.addAttribute("commandes", List.of());
            model.addAttribute("email", "Utilisateur inconnu");
        }
        return "historiques/historique";
    }
}

