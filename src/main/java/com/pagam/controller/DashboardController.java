package com.pagam.controller;

import com.pagam.entity.*;
import com.pagam.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final CapteurService capteurService;
    private final AlerteService alerteService;
    private final UtilisateurService utilisateurService;
    private final ProduitService produitService;
    private final CommandeService commandeService;
    private final VenteService venteService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // ✅ Si non authentifié, rediriger vers login
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/auth/login";
        }

        // Récupérer l'utilisateur connecté
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String email = userDetails.getUsername();
        Utilisateur user = utilisateurService.getByEmail(email);

        if (user == null) {
            return "redirect:/auth/login";
        }

        Role role = user.getRole();
        model.addAttribute("role", role.name());
        model.addAttribute("email", user.getEmail());

        // ✅ Charger les capteurs avec leurs dernières mesures
        List<Capteur> capteurs = capteurService.tousLesCapteurs();
        capteurs.forEach(c -> {
            List<Mesure> mesures = (List<Mesure>) capteurService.getDernieresMesures(c.getId());
            c.setMesures(mesures != null ? mesures : Collections.emptyList());
        });
        model.addAttribute("capteurs", capteurs);

        // ✅ Charger toutes les alertes
        List<Alerte> alertes = alerteService.getToutes();
        model.addAttribute("alertes", alertes != null ? alertes : Collections.emptyList());

        // 🔹 Redirection par rôle
        switch (role) {
            case ADMIN -> {
                List<Utilisateur> utilisateurs = utilisateurService.getAllUtilisateurs();
                utilisateurs.sort(Comparator.comparing(
                        Utilisateur::getNom,
                        Comparator.nullsLast(String::compareToIgnoreCase)
                ));
                model.addAttribute("utilisateurs", utilisateurs);

                model.addAttribute("nbUtilisateurs", utilisateurs.size());
                model.addAttribute("nbCapteurs", capteurs.size());
                model.addAttribute("nbProduits", produitService.getAllProduits().size());
                model.addAttribute("nbCommandes", commandeService.getAllCommandes().size());
                model.addAttribute("nbAlertes", alertes.size());

                List<Vente> ventes = venteService.findAll();
                model.addAttribute("ventes", ventes);

                return "dashboard/admin-home";
            }
            case AGRICULTEUR -> {
                List<Produit> produits = produitService.getAllProduits();
                produits.sort(Comparator.comparing(
                        Produit::getNom,
                        Comparator.nullsLast(String::compareToIgnoreCase)
                ));
                model.addAttribute("produits", produits);
                return "dashboard/agriculteur-home";
            }
            case ACHETEUR -> {
                List<Commande> commandes = commandeService.getAllCommandes();
                commandes.sort(Comparator.comparing(
                        Commande::getDateCommande,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ));
                model.addAttribute("commandes", commandes);
                return "dashboard/acheteur-home";
            }
            case DECIDEUR -> {
                if (alertes != null) {
                    alertes.sort(Comparator.comparing(
                            Alerte::getDateAlerte,
                            Comparator.nullsLast(Comparator.naturalOrder())
                    ));
                }
                model.addAttribute("alertes", alertes);
                return "dashboard/decideur-home";
            }
            default -> {
                // Au cas où un rôle non géré
                return "redirect:/auth/login";
            }
        }
    }

    // ---------------------- ADMIN VENTES ----------------------
    @GetMapping("/admin/ventes")
    public String ventesAdmin(Model model) {
        List<Vente> ventes = venteService.findAllVentes();
        model.addAttribute("ventes", ventes);
        return "admin/ventes";
    }

    @GetMapping("/admin-home")
    public String adminHome(Model model) {
        List<Vente> ventes = venteService.findAll();
        model.addAttribute("ventes", ventes);
        return "dashboard/admin-home";
    }
}
