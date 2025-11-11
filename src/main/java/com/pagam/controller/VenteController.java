package com.pagam.controller;

import com.pagam.entity.Produit;
import com.pagam.entity.Utilisateur;
import com.pagam.entity.Vente;
import com.pagam.service.ProduitService;
import com.pagam.service.UtilisateurService;
import com.pagam.service.VenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ventes")
public class VenteController {

    private final VenteService venteService;
    private final ProduitService produitService;
    private final UtilisateurService utilisateurService;

    // ✅ Liste des ventes (tous les utilisateurs connectés peuvent voir)
    @GetMapping
    public String listeVentes(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User user) {
            model.addAttribute("currentUser", user);
        }

        List<Vente> ventes = venteService.findAllVentes();
        model.addAttribute("ventes", ventes);

        return "ventes/liste-vente";
    }

    // ✅ Formulaire pour ajouter une vente (ADMIN uniquement)
    @GetMapping("/ajouter")
    @PreAuthorize("hasRole('ADMIN')")
    public String formulaireAjouterVente(Model model) {
        model.addAttribute("vente", new Vente());
        model.addAttribute("produits", produitService.findAll());
        model.addAttribute("utilisateurs", utilisateurService.findAll());
        return "ventes/ajout-vente";
    }

    // ✅ Ajouter une vente (ADMIN uniquement)
    @PostMapping("/ajouter")
    @PreAuthorize("hasRole('ADMIN')")
    public String ajouterVente(@ModelAttribute Vente vente) {
        Produit produit = produitService.findByIdOptional(vente.getProduit().getId())
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        Utilisateur acheteur = utilisateurService.findByIdOptional(vente.getAcheteur().getId())
                .orElseThrow(() -> new RuntimeException("Acheteur introuvable"));

        vente.setProduit(produit);
        vente.setAcheteur(acheteur);
        vente.setDateVente(LocalDateTime.now());

        venteService.save(vente);
        return "redirect:/ventes";
    }

    // ✅ Formulaire pour modifier une vente (ADMIN uniquement)
    @GetMapping("/modifier/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String formulaireModifierVente(@PathVariable Long id, Model model) {
        Vente vente = venteService.findById(id);
        if (vente == null) return "redirect:/ventes";

        model.addAttribute("vente", vente);
        model.addAttribute("produits", produitService.findAll());
        model.addAttribute("utilisateurs", utilisateurService.findAll());
        return "ventes/modifier-vente";
    }

    // ✅ Modifier une vente (ADMIN uniquement)
    @PostMapping("/modifier/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String modifierVente(@PathVariable Long id, @ModelAttribute Vente vente) {
        Vente venteExistante = venteService.findById(id);
        if (venteExistante == null) return "redirect:/ventes";

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
        return "redirect:/ventes";
    }

    // ✅ Supprimer une vente (ADMIN uniquement)
    @GetMapping("/supprimer/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String supprimerVente(@PathVariable Long id) {
        venteService.deleteById(id);
        return "redirect:/ventes";
    }
}
