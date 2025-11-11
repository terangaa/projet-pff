package com.pagam.controller;

import com.pagam.entity.*;
import com.pagam.repository.CommandeRepository;
import com.pagam.repository.UtilisateurRepository;
import com.pagam.repository.VenteRepository;
import com.pagam.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/commandes")
public class CommandeController {

    private final CommandeService commandeService;
    private final UtilisateurService utilisateurService;
    private final VenteRepository venteRepository;
    private final CommandeRepository commandeRepository;
    private final ProduitService produitService;
    private final PanierService panierService;
    private final VenteService venteService;
    private final UtilisateurRepository utilisateurRepository;

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

    @DeleteMapping("/{id}/supprimer")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> supprimerCommandeAjax(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!commandeRepository.existsById(id)) {
                response.put("success", false);
                response.put("message", "Commande introuvable !");
                return ResponseEntity.status(404).body(response);
            }

            commandeRepository.deleteById(id);
            response.put("success", true);
            response.put("message", "✅ Commande supprimée avec succès !");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "❌ Erreur lors de la suppression : " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
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

    @PostMapping("/{id}/valider")
    @ResponseBody
    public ResponseEntity<?> validerCommande(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Commande commande = commandeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

            if (commande.getStatut() == StatutCommande.VALIDEE) {
                response.put("success", false);
                response.put("message", "Cette commande est déjà validée");
                return ResponseEntity.badRequest().body(response);
            }

            // Créer la vente
            Vente vente = new Vente();
            vente.setCommande(commande);
            vente.setDateVente(LocalDateTime.now());
            vente.setQuantite(commande.getQuantite());
            vente.setPrix(commande.getPrixUnitaire());
            vente.setMontantTotal(commande.getPrixTotal());
            vente.setStatut("COMPLETEE");
            venteRepository.save(vente);

            // Mettre à jour le statut et réduire le stock
            commande.setStatut(StatutCommande.VALIDEE);
            Produit produit = commande.getProduit();
            produit.setStock(produit.getStock() - commande.getQuantite());
            commandeRepository.save(commande);
            produitService.saveProduit(produit); // ⚡ sauvegarde du produit avec le stock à jour

            // Réponse Ajax avec stock mis à jour
            response.put("success", true);
            response.put("message", "Commande validée et vente créée avec succès");
            response.put("stock", produit.getStock());
            response.put("produitId", produit.getId());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/{id}/annuler")
    @ResponseBody
    public ResponseEntity<?> annulerCommande(@PathVariable Long id) {
        try {
            Commande commande = commandeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

            // Vérifier si la commande peut être annulée
            if (commande.getStatut() == StatutCommande.VALIDEE) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Impossible d'annuler une commande déjà validée");
                return ResponseEntity.badRequest().body(response);
            }

            // Mettre à jour le statut
            commande.setStatut(StatutCommande.ANNULEE);
            commandeRepository.save(commande);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Commande annulée avec succès");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


}
