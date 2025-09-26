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

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            model.addAttribute("role", "VISITEUR");
            return "dashboard/dashboard";
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String email = userDetails.getUsername();
        Utilisateur user = utilisateurService.getByEmail(email);

        if (user == null) {
            model.addAttribute("role", "VISITEUR");
            return "dashboard/dashboard";
        }

        Role role = user.getRole();
        model.addAttribute("role", role.name());
        model.addAttribute("email", user.getEmail());

        // Capteurs
        List<Capteur> capteurs = capteurService.tousLesCapteurs();
        capteurs.forEach(c -> {
            List<Mesure> mesures = (List<Mesure>) capteurService.getDernieresMesures(c.getId());
            c.setMesures(mesures != null ? mesures : Collections.emptyList());
        });
        model.addAttribute("capteurs", capteurs);

        // Alertes
        List<Alerte> alertes = alerteService.getToutes();
        model.addAttribute("alertes", alertes != null ? alertes : Collections.emptyList());

        switch (role) {
            case ADMIN -> {
                List<Utilisateur> utilisateurs = utilisateurService.getAllUtilisateurs();
                utilisateurs.sort(Comparator.comparing(
                        Utilisateur::getNom,
                        Comparator.nullsLast(String::compareToIgnoreCase)
                ));
                model.addAttribute("utilisateurs", utilisateurs);

                // 🔥 ajouter les ventes
                List<Vente> ventes = venteService.findAll();
                model.addAttribute("ventes", ventes);
                return "dashboard/admin-home"; // 🚀 Page spéciale ADMIN
            }
            case AGRICULTEUR -> {
                List<Produit> produits = produitService.getAllProduits();
                produits.sort(Comparator.comparing(
                        Produit::getNom,
                        Comparator.nullsLast(String::compareToIgnoreCase)
                ));
                model.addAttribute("produits", produits);
                return "dashboard/agriculteur-home"; // 🚀 Page spéciale AGRICULTEUR
            }
            case ACHETEUR -> {
                List<Commande> commandes = commandeService.getAllCommandes();
                commandes.sort(Comparator.comparing(
                        Commande::getDateCommande,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ));
                model.addAttribute("commandes", commandes);
                return "dashboard/acheteur-home"; // 🚀 Page spéciale ACHETEUR
            }
            case DECIDEUR -> {
                if (alertes != null) {
                    alertes.sort(Comparator.comparing(
                            Alerte::getDateAlerte,
                            Comparator.nullsLast(Comparator.naturalOrder())
                    ));
                }
                model.addAttribute("alertes", alertes);
                return "dashboard/decideur-home"; // 🚀 Page spéciale DECIDEUR
            }
        }

        return "dashboard/dashboard";
    }

    @GetMapping("/admin/ventes")
    public String ventesAdmin(Model model) {
        List<Vente> ventes = venteService.findAllVentes(); // Toutes les ventes
        model.addAttribute("ventes", ventes);
        return "admin/ventes"; // le JSP/HTML ci-dessus
    }

    @GetMapping("/admin-home")
    public String adminHome(Model model) {
        List<Vente> ventes = venteService.findAll(); // récupère toutes les ventes
        model.addAttribute("ventes", ventes);        // ⚠️ ajouter au modèle
        return "dashboard/admin-home";
    }
}