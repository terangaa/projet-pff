package com.pagam.controller;

import com.pagam.entity.Produit;
import com.pagam.entity.Vente;
import com.pagam.service.ProduitService;
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

    // 1️⃣ Afficher la liste des ventes
    @GetMapping
    public String listeVentes(Model model) {
        List<Vente> ventes = venteService.findAll();
        model.addAttribute("ventes", ventes);
        return "ventes/liste-vente";
    }

    // 2️⃣ Formulaire pour ajouter une vente
    @GetMapping("/ajouter")
    public String formulaireAjouterVente(Model model) {
        model.addAttribute("vente", new Vente());
        model.addAttribute("produits", produitService.findAll());
        return "ventes/ajout-vente";
    }

    // 3️⃣ Sauvegarder une vente
    @PostMapping("/ajouter")
    public String ajouterVente(@ModelAttribute Vente vente,
                               @RequestParam(required = false) Double prixUnitaire) {
        if (prixUnitaire != null) {
            vente.setPrix(prixUnitaire);
        } else if (vente.getProduit() != null) {
            vente.setPrix(vente.getProduit().getPrix());
        }
        vente.setMontantTotal(vente.getPrix() * vente.getQuantite());
        vente.setDateVente(LocalDateTime.now());
        // Optionnel : définir l’acheteur connecté ici si tu as un service utilisateur
        venteService.save(vente);
        return "redirect:/agriculteur/ventes";
    }

    // 4️⃣ Supprimer une vente
    @GetMapping("/supprimer/{id}")
    public String supprimerVente(@PathVariable Long id) {
        venteService.deleteById(id);
        return "redirect:/agriculteur/ventes";
    }
}
