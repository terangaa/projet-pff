package com.pagam.controller;

import com.pagam.entity.Commande;
import com.pagam.entity.Produit;
import com.pagam.entity.Utilisateur;
import com.pagam.service.CommandeService;
import com.pagam.service.ProduitService;
import com.pagam.service.UtilisateurService;
import com.pagam.service.VenteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/commandes")
public class CommandeController {

    private final CommandeService commandeService;
    private final UtilisateurService utilisateurService;
    private final ProduitService produitService;
    private final VenteService venteService;

    public CommandeController(CommandeService commandeService,
                              UtilisateurService utilisateurService,
                              ProduitService produitService,
                              VenteService venteService) {
        this.commandeService = commandeService;
        this.utilisateurService = utilisateurService;
        this.produitService = produitService;
        this.venteService = venteService;
    }

    // Liste des commandes
    @GetMapping
    public String listeCommandes(Model model) {
        List<Commande> commandes = commandeService.getAllCommandes();
        model.addAttribute("commandes", commandes);
        return "commandes/commande";
    }

    // Formulaire pour ajouter une commande
    // Formulaire pour ajouter une commande
    @GetMapping("/nouvelle")
    public String nouvelleCommande(Model model) {
        model.addAttribute("commande", new Commande());

        // Filtrer seulement les acheteurs
        List<Utilisateur> acheteurs = utilisateurService.getAllUtilisateurs()
                .stream()
                .filter(u -> "ACHETEUR".equals(u.getRole()))
                .toList();
        model.addAttribute("utilisateurs", acheteurs);

        model.addAttribute("produits", produitService.getAllProduits());
        return "commandes/commande-form";
    }

    // Afficher formulaire de modification
    @GetMapping("/modifier/{id}")
    public String afficherFormModifier(@PathVariable Long id, Model model) {
        Commande commande = commandeService.getCommandeById(id);
        model.addAttribute("commande", commande);

        model.addAttribute("produits", produitService.getAllProduits());

        // Filtrer seulement les acheteurs
        List<Utilisateur> acheteurs = utilisateurService.getAllUtilisateurs()
                .stream()
                .filter(u -> "ACHETEUR".equals(u.getRole()))
                .toList();
        model.addAttribute("utilisateurs", acheteurs);

        return "commandes/commande-form";
    }
    // Enregistrer une commande
    @PostMapping("/enregistrer")
    public String enregistrerCommande(@ModelAttribute Commande commande) {
        if (commande.getProduit() != null && commande.getProduit().getId() != null) {
            Produit produit = produitService.getProduitById(commande.getProduit().getId());
            commande.setProduit(produit);
        }
        if (commande.getAcheteur() != null && commande.getAcheteur().getId() != null) {
            Utilisateur acheteur = utilisateurService.getUtilisateurById(commande.getAcheteur().getId());
            commande.setAcheteur(acheteur);
        }

        int quantite = (commande.getQuantite() != null) ? commande.getQuantite() : 0;
        double prixProduit = (commande.getProduit() != null && commande.getProduit().getPrix() != null)
                ? commande.getProduit().getPrix()
                : 0.0;
        commande.setPrixTotal(prixProduit * quantite);

        if (commande.getDateCommande() == null) {
            commande.setDateCommande(LocalDateTime.now());
        }

        commandeService.saveCommande(commande);
        return "redirect:/commandes";
    }

    // Supprimer une commande
    @GetMapping("/supprimer/{id}")
    public String supprimerCommande(@PathVariable Long id) {
        commandeService.deleteCommande(id);
        return "redirect:/commandes";
    }

    // Valider une commande et créer une vente correspondante
    @GetMapping("/valider/{id}")
    public String validerCommande(@PathVariable Long id) {
        Commande commande = commandeService.getCommandeById(id);
        if (commande != null && (commande.getVentes() == null || commande.getVentes().isEmpty())) {
            venteService.creerVenteDepuisCommande(commande);
        }
        return "redirect:/commandes";
    }
}
