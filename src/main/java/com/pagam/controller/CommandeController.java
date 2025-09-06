package com.pagam.controller;

import com.pagam.entity.Commande;
import com.pagam.entity.Produit;
import com.pagam.entity.Utilisateur;
import com.pagam.service.CommandeService;
import com.pagam.service.ProduitService;
import com.pagam.service.UtilisateurService;
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

    public CommandeController(CommandeService commandeService,
                              UtilisateurService utilisateurService,
                              ProduitService produitService) {
        this.commandeService = commandeService;
        this.utilisateurService = utilisateurService;
        this.produitService = produitService;
    }

    // Liste des commandes
    @GetMapping
    public String listeCommandes(Model model) {
        List<Commande> commandes = commandeService.getAllCommandes();
        model.addAttribute("commandes", commandes);
        return "commandes/commande";
    }

    // Formulaire pour ajouter une nouvelle commande
    @GetMapping("/nouvelle")
    public String nouvelleCommande(Model model) {
        Commande commande = new Commande();
        model.addAttribute("commande", commande);
        model.addAttribute("utilisateurs", utilisateurService.getAllUtilisateurs());
        model.addAttribute("produits", produitService.getAllProduits());
        return "commandes/commande-form";
    }

    // Formulaire pour modifier une commande existante
    @GetMapping("/modifier/{id}")
    public String modifierCommande(@PathVariable Long id, Model model) {
        Commande commande = commandeService.getCommandeById(id);

        // Gestion de la date
        Date dateCommande = (commande.getDateCommande() != null)
                ? Date.from(commande.getDateCommande().atZone(ZoneId.systemDefault()).toInstant())
                : new Date();
        model.addAttribute("dateCommande", dateCommande);

        model.addAttribute("commande", commande);
        model.addAttribute("utilisateurs", utilisateurService.getAllUtilisateurs());
        model.addAttribute("produits", produitService.getAllProduits());
        return "commandes/commande-form";
    }

    // Enregistrer une commande (nouvelle ou modification)
    @PostMapping("/enregistrer")
    public String enregistrerCommande(@ModelAttribute Commande commande) {

        // Récupérer le produit complet depuis la base
        if (commande.getProduit() != null && commande.getProduit().getId() != null) {
            Produit produit = produitService.getProduitById(commande.getProduit().getId());
            commande.setProduit(produit);
        }

        // Récupérer l'acheteur complet depuis la base
        if (commande.getAcheteur() != null && commande.getAcheteur().getId() != null) {
            Utilisateur acheteur = utilisateurService.getUtilisateurById(commande.getAcheteur().getId());
            commande.setAcheteur(acheteur);
        }

        // Calculer le prix total en évitant les nulls
        int quantite = (commande.getQuantite() != null) ? commande.getQuantite() : 0;
        double prixProduit = (commande.getProduit() != null && commande.getProduit().getPrix() != null)
                ? commande.getProduit().getPrix()
                : 0.0;
        commande.setPrixTotal(prixProduit * quantite);

        // Définir la date si null
        if (commande.getDateCommande() == null) {
            commande.setDateCommande(LocalDateTime.now());
        }

        // Enregistrer la commande
        commandeService.saveCommande(commande);

        return "redirect:/commandes";
    }

    // Supprimer une commande
    @GetMapping("/supprimer/{id}")
    public String supprimerCommande(@PathVariable Long id) {
        commandeService.deleteCommande(id);
        return "redirect:/commandes";
    }
}
