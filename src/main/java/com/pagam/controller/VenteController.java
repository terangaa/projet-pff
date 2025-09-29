package com.pagam.controller;

import com.pagam.entity.Produit;
import com.pagam.entity.Utilisateur;
import com.pagam.entity.Vente;
import com.pagam.service.ProduitService;
import com.pagam.service.UtilisateurService;
import com.pagam.service.VenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/agriculteur/ventes")
public class VenteController {

    private final VenteService venteService;
    private final ProduitService produitService;
    private final UtilisateurService utilisateurService;

    // Liste des ventes
    @GetMapping
    public String listeVentes(Model model) {
        List<Vente> ventes = venteService.findAllVentes();
        model.addAttribute("ventes", ventes);
        return "ventes/liste-vente";
    }

    // Formulaire pour ajouter une vente
    @GetMapping("/ajouter")
    public String formulaireAjouterVente(Model model) {
        model.addAttribute("vente", new Vente());
        model.addAttribute("produits", produitService.findAll());
        model.addAttribute("utilisateurs", utilisateurService.findAll());
        return "ventes/ajout-vente";
    }

    // Ajouter une vente
    @PostMapping("/ajouter")
    public String ajouterVente(@ModelAttribute Vente vente) {
        // Récupérer le produit et l'acheteur via Optional
        Produit produit = produitService.findByIdOptional(vente.getProduit().getId())
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        Utilisateur acheteur = utilisateurService.findByIdOptional(vente.getAcheteur().getId())
                .orElseThrow(() -> new RuntimeException("Acheteur introuvable"));

        // Assigner les objets correctement
        vente.setProduit(produit);
        vente.setAcheteur(acheteur);
        vente.setDateVente(LocalDateTime.now());

        venteService.save(vente);
        return "redirect:/agriculteur/ventes";
    }

    // Formulaire pour modifier une vente
    @GetMapping("/modifier/{id}")
    public String formulaireModifierVente(@PathVariable Long id, Model model) {
        Vente vente = venteService.findById(id);
        if (vente == null) return "redirect:/agriculteur/ventes";

        model.addAttribute("vente", vente);
        model.addAttribute("produits", produitService.findAll());
        model.addAttribute("utilisateurs", utilisateurService.findAll());
        return "ventes/modifier-vente";
    }

    // Modifier une vente
    @PostMapping("/modifier/{id}")
    public String modifierVente(@PathVariable Long id, @ModelAttribute Vente vente) {
        Vente venteExistante = venteService.findById(id);
        if (venteExistante == null) return "redirect:/agriculteur/ventes";

        Produit produit = produitService.findByIdOptional(vente.getProduit().getId())
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));
        Utilisateur acheteur = utilisateurService.findByIdOptional(vente.getAcheteur().getId())
                .orElseThrow(() -> new RuntimeException("Acheteur introuvable"));

        venteExistante.setProduit(produit);
        venteExistante.setAcheteur(acheteur);
        venteExistante.setQuantite(vente.getQuantite());
        venteExistante.setPrix(vente.getPrix());
        venteExistante.setDateVente(LocalDateTime.now());

        venteService.save(venteExistante);
        return "redirect:/agriculteur/ventes";
    }

    // Supprimer une vente
    @GetMapping("/supprimer/{id}")
    public String supprimerVente(@PathVariable Long id) {
        venteService.deleteById(id);
        return "redirect:/agriculteur/ventes";
    }
}
