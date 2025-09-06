package com.pagam.controller;

import com.pagam.entity.Commande;
import com.pagam.entity.Produit;
import com.pagam.service.MarketplaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marketplace")
@RequiredArgsConstructor
public class MarketplaceController {

    private final MarketplaceService marketplaceService;

    @PostMapping("/produits/{producteurId}")
    public Produit ajouterProduit(@RequestBody Produit produit, @PathVariable Long producteurId) {
        return marketplaceService.ajouterProduit(produit, producteurId);
    }

    @GetMapping("/produits")
    public List<Produit> listerProduits() {
        return marketplaceService.listerProduits();
    }

    @PostMapping("/commande/{acheteurId}/{produitId}")
    public Commande passerCommande(@PathVariable Long acheteurId,
                                   @PathVariable Long produitId,
                                   @RequestParam int quantite) {
        return marketplaceService.passerCommande(acheteurId, produitId, quantite);
    }

    @GetMapping("/commandes/{acheteurId}")
    public List<Commande> listerCommandes(@PathVariable Long acheteurId) {
        return marketplaceService.listerCommandesAcheteur(acheteurId);
    }
}
