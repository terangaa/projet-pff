package com.pagam.controller;

import com.pagam.entity.Commande;
import com.pagam.entity.Produit;
import com.pagam.entity.StatutCommande;
import com.pagam.entity.Utilisateur;
import com.pagam.service.CommandeService;
import com.pagam.service.ProduitService;
import com.pagam.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AcheteurController {

    private final ProduitService produitService;
    private final CommandeService commandeService;
    private final UtilisateurService utilisateurService;

    // ✅ Affichage du catalogue
    @GetMapping("/achats")
    public String afficherCatalogue(Model model, Principal principal) {
        List<Produit> produits = produitService.findAll();
        model.addAttribute("produits", produits);

        if (principal != null) {
            model.addAttribute("email", principal.getName());
        }

        return "achats/achat"; // ta page catalogue
    }

    // ✅ Ajouter une commande
    @PostMapping("/achats/ajouter")
    public String ajouterCommande(@RequestParam Long idProduit,
                                  @RequestParam int quantite,
                                  Principal principal) {

        Produit produit = produitService.findById(idProduit);

        if (produit == null) {
            // produit non trouvé
            return "redirect:/achats?error=produit_introuvable";
        }

        if (quantite <= 0 || quantite > produit.getStock()) {
            // quantité invalide ou dépasse le stock
            return "redirect:/achats?error=quantite_invalide";
        }

        Utilisateur acheteur = utilisateurService.getUtilisateurByEmail(principal.getName());

        Commande commande = new Commande();
        commande.setProduit(produit);
        commande.setAcheteur(acheteur);
        commande.setQuantite(quantite);
        commande.setStatut(StatutCommande.EN_COURS); // statut initial

        // Sauvegarde la commande (prix et date calculés via @PrePersist)
        commandeService.save(commande);

        // 🔹 Mise à jour du stock du produit
        produit.setStock(produit.getStock() - quantite);
        produitService.save(produit);

        return "redirect:/achats/mes";
    }

    // ✅ Afficher les commandes de l’acheteur connecté
    @GetMapping("/achats/mes")
    public String mesCommandes(Model model, Principal principal) {
        Utilisateur acheteur = utilisateurService.getUtilisateurByEmail(principal.getName());
        List<Commande> commandes = commandeService.getCommandesByAcheteur(acheteur);

        model.addAttribute("commandes", commandes);
        model.addAttribute("email", principal.getName());

        return "achats/mes-commandes";
    }

}
