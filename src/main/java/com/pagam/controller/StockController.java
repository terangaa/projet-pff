package com.pagam.controller;

import com.pagam.entity.Produit;
import com.pagam.entity.Producteur;
import com.pagam.repository.ProduitRepository;
import com.pagam.service.ProduitService;
import com.pagam.service.ProducteurService;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class StockController {

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private ProduitService produitService;

    @Autowired
    private ProducteurService producteurService;

    // ✅ Ajout ou mise à jour de stock par AJAX
    @PostMapping("/ajouter-stock-ajax")
    @ResponseBody
    @Secured("ROLE_AGRICULTEUR")
    public String ajouterStockAjax(@RequestParam("produitId") Long produitId,
                                   @RequestParam("quantite") double quantiteAjoutee,
                                   Principal principal) {

        Produit produitSource = produitService.findById(produitId);
        if (produitSource == null || produitSource.getAgriculteur() == null) {
            return "❌ Erreur : produit introuvable ou non fourni par un producteur.";
        }

        Producteur producteur = producteurService.findByUtilisateurEmail(principal.getName());
        if (producteur == null) {
            return "❌ Erreur : producteur introuvable.";
        }

        Optional<Produit> optProduitExistant =
                produitRepository.findByNomAndAgriculteur(produitSource.getNom(), producteur);

        if (optProduitExistant.isPresent()) {
            Produit produitExistant = optProduitExistant.get();
            produitExistant.setStock(produitExistant.getStock() + (int) quantiteAjoutee);
            produitRepository.save(produitExistant);
            return "✅ Stock mis à jour avec succès !";
        } else {
            Produit nouveauProduit = new Produit();
            nouveauProduit.setNom(produitSource.getNom());
            nouveauProduit.setDescription(produitSource.getDescription());
            nouveauProduit.setPrix(produitSource.getPrix());
            nouveauProduit.setStock((int) quantiteAjoutee);
            nouveauProduit.setAgriculteur(producteur);
            produitRepository.save(nouveauProduit);
            return "✅ Produit ajouté avec succès !";
        }
    }

    // ✅ Mise à jour directe (depuis formulaire)
    @PostMapping("/modifier-stock/{id}")
    @ResponseBody
    public String modifierStock(@PathVariable("id") Long id, @RequestParam("stock") int nouveauStock) {
        Produit produit = produitRepository.findById(id).orElse(null);
        if (produit != null) {
            produit.setStock(nouveauStock);
            produitRepository.save(produit);
            return "OK";  // ← succès
        }
        return "ERREUR";  // ← erreur
    }


    // ✅ Page d’affichage du stock (admin uniquement)
    @GetMapping("/stock")
    @Secured("ROLE_ADMIN")
    public String produitsStock(Model model) {
        List<Produit> produits = produitService.getAllProduits();
        int totalStock = produits.stream().mapToInt(Produit::getStock).sum();

        model.addAttribute("produits", produits);
        model.addAttribute("totalStock", totalStock);
        return "produits/stock";
    }
}