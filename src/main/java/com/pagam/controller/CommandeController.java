package com.pagam.controller;

import com.pagam.entity.*;
import com.pagam.service.CommandeService;
import com.pagam.service.ProduitService;
import com.pagam.service.UtilisateurService;
import com.pagam.service.VenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/commandes")
public class CommandeController {

    private final CommandeService commandeService;
    private final UtilisateurService utilisateurService;
    private final ProduitService produitService;
    private final VenteService venteService;

    // 🔹 Liste des commandes
    @GetMapping
    public String listeCommandes(Model model,
                                 @ModelAttribute("success") String success,
                                 @ModelAttribute("error") String error) {
        List<Commande> commandes = commandeService.getAllCommandes();
        model.addAttribute("commandes", commandes);

        if (success != null && !success.isEmpty())
            model.addAttribute("success", success);

        if (error != null && !error.isEmpty())
            model.addAttribute("error", error);

        return "commandes/commande";
    }

    // 🔹 Formulaire pour ajouter une commande
    @GetMapping("/nouvelle")
    public String nouvelleCommande(Model model) {
        model.addAttribute("commande", new Commande());

        List<Utilisateur> acheteurs = utilisateurService.getAllUtilisateurs()
                .stream()
                .filter(u -> u.getRole() == Role.ACHETEUR)
                .toList();
        model.addAttribute("utilisateurs", acheteurs);

        model.addAttribute("produits", produitService.getAllProduits());
        return "commandes/commande-form";
    }

    // 🔹 Modifier une commande existante
    @GetMapping("/modifier/{id}")
    public String afficherFormModifier(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Commande commande = commandeService.getCommandeById(id);
        if (commande == null) {
            redirectAttributes.addFlashAttribute("error", "❌ Commande introuvable !");
            return "redirect:/commandes";
        }

        model.addAttribute("commande", commande);
        model.addAttribute("produits", produitService.getAllProduits());

        List<Utilisateur> acheteurs = utilisateurService.getAllUtilisateurs()
                .stream()
                .filter(u -> u.getRole() == Role.ACHETEUR)
                .toList();
        model.addAttribute("utilisateurs", acheteurs);

        return "commandes/commande-form";
    }

    // 🔹 Enregistrer une commande (ajout ou modification)
    @PostMapping("/enregistrer")
    public String enregistrerCommande(@ModelAttribute Commande commande, RedirectAttributes redirectAttributes) {
        try {
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
            redirectAttributes.addFlashAttribute("success", "✅ Commande enregistrée avec succès !");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "❌ Erreur : " + e.getMessage());
            return "redirect:/commandes/nouvelle";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "⚠️ Une erreur inattendue est survenue.");
            return "redirect:/commandes/nouvelle";
        }

        return "redirect:/commandes";
    }

    // 🔹 Supprimer une commande
    @GetMapping("/supprimer/{id}")
    public String supprimerCommande(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            commandeService.deleteCommande(id);
            redirectAttributes.addFlashAttribute("success", "✅ Commande supprimée avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Impossible de supprimer la commande : " + e.getMessage());
        }
        return "redirect:/commandes";
    }

    // 🔹 Valider une commande et créer une vente correspondante
    @GetMapping("/valider/{id}")
    public String validerCommande(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Commande commande = commandeService.getCommandeById(id);
            if (commande == null) {
                redirectAttributes.addFlashAttribute("error", "❌ Commande introuvable !");
                return "redirect:/commandes";
            }

            if (commande.getVentes() != null && !commande.getVentes().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "⚠️ Cette commande a déjà été validée !");
                return "redirect:/commandes";
            }

            venteService.creerVenteDepuisCommande(commande);

            // 🔹 Mise à jour du statut
            commande.setStatut(StatutCommande.VALIDEE);
            commandeService.saveCommande(commande);

            redirectAttributes.addFlashAttribute("success", "✅ Commande validée et vente créée !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "❌ Erreur lors de la validation : " + e.getMessage());
        }

        return "redirect:/commandes";
    }
}
