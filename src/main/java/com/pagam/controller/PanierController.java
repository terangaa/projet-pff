package com.pagam.controller;

import com.pagam.entity.*;
import com.pagam.repository.CommandeRepository;
import com.pagam.repository.ProduitRepository;
import com.pagam.service.PanierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/panier")
@RequiredArgsConstructor
public class PanierController {

    private final PanierService panierService;
    private final ProduitRepository produitRepository;
    private final CommandeRepository commandeRepository;

    // Affichage de la page panier
    @GetMapping
    public String afficherPanier(Model model) {
        Panier panier = panierService.getPanierFromSession();
        model.addAttribute("articles", panier.getItems());
        model.addAttribute("total", panier.getTotal());
        model.addAttribute("nombreArticles", panier.getItems().size());
        return "panier/panier";
    }

    // Ajouter un produit au panier
    @PostMapping("/ajouter/{produitId}")
    public ResponseEntity<?> ajouterAuPanier(@PathVariable Long produitId,
                                             @RequestParam(defaultValue = "1") Integer quantite) {
        try {
            panierService.ajouterProduit(produitId, quantite);
            Panier panier = panierService.getPanierFromSession();
            return ResponseEntity.ok(panierSummary(panier));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Modifier la quantité d'un produit
    @PostMapping("/modifier/{produitId}")
    public ResponseEntity<?> modifierQuantite(@PathVariable Long produitId,
                                              @RequestParam Integer quantite) {
        try {
            panierService.modifierQuantite(produitId, quantite);
            Panier panier = panierService.getPanierFromSession();
            return ResponseEntity.ok(panierSummary(panier));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Supprimer un produit
    @DeleteMapping("/supprimer/{produitId}")
    public ResponseEntity<?> supprimerDuPanier(@PathVariable Long produitId) {
        try {
            panierService.supprimerProduit(produitId);
            Panier panier = panierService.getPanierFromSession();
            return ResponseEntity.ok(panierSummary(panier));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Vider le panier
    @PostMapping("/vider")
    public ResponseEntity<?> viderPanier() {
        panierService.viderPanier();
        Panier panier = panierService.getPanierFromSession();
        return ResponseEntity.ok(panierSummary(panier));
    }

    // Nombre d'articles
    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<?> getNombreArticles() {
        Panier panier = panierService.getPanierFromSession();
        return ResponseEntity.ok(panierSummary(panier));
    }

    // Valider le panier
    @PostMapping("/valider")
    public ResponseEntity<?> validerPanier(@SessionAttribute(name = "utilisateur", required = false) Utilisateur acheteur) {
        if (acheteur == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Vous devez être connecté pour valider le panier.");
        }

        try {
            panierService.validerPanier(acheteur);
            Panier panier = panierService.getPanierFromSession();
            return ResponseEntity.ok("Panier validé et commandes créées !");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Méthode utilitaire pour résumé du panier
    private PanierSummary panierSummary(Panier panier) {
        return new PanierSummary(panier.getItems().size(), panier.getTotal());
    }

    // DTO pour résumé du panier
    private record PanierSummary(int nombreArticles, double total) {}
}
