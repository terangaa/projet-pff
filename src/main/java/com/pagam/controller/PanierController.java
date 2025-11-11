package com.pagam.controller;

import com.pagam.entity.Panier;
import com.pagam.service.PanierService;
import com.pagam.repository.CommandeRepository;
import com.pagam.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/panier")
@RequiredArgsConstructor
public class PanierController {

    private final PanierService panierService;
    private final ProduitRepository produitRepository;
    private final CommandeRepository commandeRepository;


    @GetMapping
    public String afficherPanier(Model model) {
        Panier panier = panierService.getPanierFromSession();
        model.addAttribute("articles", panier.getItems());
        model.addAttribute("total", panier.getTotal());
        model.addAttribute("nombreArticles", panier.getItems().size());
        return "panier/panier";
    }

    @PostMapping("/ajouter/{produitId}")
    public ResponseEntity<?> ajouterAuPanier(@PathVariable Long produitId,
                                             @RequestParam(defaultValue = "1") Integer quantite) {
        try {
            panierService.ajouterProduit(produitId, quantite);
            Panier panier = panierService.getPanierFromSession();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Produit ajouté au panier");
            response.put("totalItems", panier.getItems().size());
            response.put("total", panier.getTotal());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/modifier/{produitId}")
    public ResponseEntity<?> modifierQuantite(@PathVariable Long produitId,
                                              @RequestParam Integer quantite) {
        try {
            panierService.modifierQuantite(produitId, quantite);
            Panier panier = panierService.getPanierFromSession();
            return ResponseEntity.ok(new PanierSummary(panier.getItems().size(), panier.getTotal()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/supprimer/{produitId}")
    public ResponseEntity<?> supprimerDuPanier(@PathVariable Long produitId) {
        try {
            panierService.supprimerProduit(produitId);
            Panier panier = panierService.getPanierFromSession();
            return ResponseEntity.ok(new PanierSummary(panier.getItems().size(), panier.getTotal()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/vider")
    public ResponseEntity<?> viderPanier() {
        panierService.viderPanier();
        Panier panier = panierService.getPanierFromSession();
        return ResponseEntity.ok(new PanierSummary(panier.getItems().size(), panier.getTotal()));
    }

    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<?> getNombreArticles() {
        Panier panier = panierService.getPanierFromSession();
        return ResponseEntity.ok(new PanierSummary(panier.getItems().size(), panier.getTotal()));
    }

    @PostMapping("/valider")
    public ResponseEntity<?> validerPanier() {
        try {
            // Récupérer l'utilisateur connecté
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            System.out.println("=== VALIDATION DU PANIER ===");
            System.out.println("Utilisateur: " + email);

            // Appeler avec l'email
            panierService.validerPanier(email);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Panier validé et commandes créées !");
            response.put("redirectUrl", "/commandes");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("ERREUR validation panier: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    private record PanierSummary(int nombreArticles, double total) {}
}