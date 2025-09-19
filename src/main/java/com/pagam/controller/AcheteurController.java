package com.pagam.controller;

import com.pagam.entity.Commande;
import com.pagam.entity.Produit;
import com.pagam.entity.Utilisateur;
import com.pagam.service.CommandeService;
import com.pagam.service.ProduitService;
import com.pagam.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AcheteurController {

    private final ProduitService produitService;
    private final CommandeService commandeService;
    private final UtilisateurService utilisateurService;

    // ✅ Catalogue des produits
    @GetMapping("/achats")
    public String afficherCatalogue(Model model, Principal principal) {
        List<Produit> produits = produitService.findAll();
        model.addAttribute("produits", produits);

        // Email de l’utilisateur connecté
        if (principal != null) {
            model.addAttribute("email", principal.getName());
        }

        return "achats/achat"; // correspond à catalogue.html
    }

    // ✅ Ajouter une commande depuis le catalogue
    @GetMapping("/achats/ajouter/{idProduit}")
    public String ajouterCommandeDepuisCatalogue(@PathVariable Long idProduit,
                                                 Principal principal) {
        Produit produit = produitService.getProduitById(idProduit);
        Utilisateur acheteur = utilisateurService.getUtilisateurByEmail(principal.getName());

        Commande commande = new Commande();
        commande.setProduit(produit);
        commande.setAcheteur(acheteur);
        commande.setQuantite(1);
        commande.setPrixTotal(produit.getPrix());
        commande.setDateCommande(LocalDateTime.now());

        commandeService.saveCommande(commande);

        return "redirect:/achats/mes"; // redirection corrigée
    }

    // ✅ Lister uniquement les commandes de l’acheteur connecté
    @GetMapping("/achats/mes")
    public String mesCommandes(Model model, Principal principal) {
        Utilisateur acheteur = utilisateurService.getUtilisateurByEmail(principal.getName());
        List<Commande> commandes = commandeService.getCommandesByAcheteur(acheteur);

        model.addAttribute("commandes", commandes);
        model.addAttribute("email", principal.getName());

        return "achats/mes-commandes";
    }

}
