package com.pagam.controller;

import com.pagam.service.PanierService;
import com.pagam.service.ProduitService;
import com.pagam.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class AcheteurController {

    private final ProduitService produitService;
    private final PanierService panierService; // <-- injecté
    private final UtilisateurService utilisateurService;

    // Affichage du catalogue
    @GetMapping("/achats")
    public String afficherCatalogue(Model model, Principal principal) {
        model.addAttribute("produits", produitService.findAll());
        if (principal != null) model.addAttribute("email", principal.getName());
        return "achats/achat";
    }

    // Ajouter un produit au panier (pas de commande directe)
    @PostMapping("/achats/ajouter")
    public String ajouterAuPanier(@RequestParam Long idProduit,
                                  @RequestParam int quantite,
                                  RedirectAttributes redirectAttributes) {
        try {
            panierService.ajouterProduit(idProduit, quantite);
            redirectAttributes.addFlashAttribute("messageSuccess", "✅ Produit ajouté au panier avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("messageError", "❌ Erreur : impossible d’ajouter le produit au panier.");
        }
        return "redirect:/panier";
    }

    // Afficher le panier
//    // Valider le panier → création de commandes
//    @PostMapping("/panier/valider")
//    public String validerPanier(Principal principal) {
//        try {
//            panierService.validerPanier(principal.getName());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "redirect:/achats/mes"; // redirige vers les commandes
//    }

    @GetMapping("/achats/mes")
    public String mesCommandes(Model model, Principal principal) throws Exception {
        if (principal != null) {
            // récupérer les commandes de l'utilisateur connecté
            var commandes = panierService.listerCommandesUtilisateur(principal.getName());
            model.addAttribute("commandes", commandes);
            model.addAttribute("email", principal.getName());
        }
        return "achats/mes-commandes"; // le fichier Thymeleaf à créer : achats/mes-commandes.html
    }

}
