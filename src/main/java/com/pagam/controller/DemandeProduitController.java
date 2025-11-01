package com.pagam.controller;

import com.pagam.entity.DemandeProduit;
import com.pagam.service.DemandeProduitService;
import com.pagam.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/produits/demande")
@RequiredArgsConstructor
public class DemandeProduitController {

    private final DemandeProduitService demandeService;
    private final EmailService emailService;

    // 📩 Envoi d'une nouvelle demande par un utilisateur
    @PostMapping("/envoyer")
    public String envoyerDemande(@RequestParam("nomProduit") String nomProduit,
                                 @RequestParam(value = "message", required = false) String message,
                                 RedirectAttributes redirectAttributes) {
        // Création de la demande
        DemandeProduit demande = new DemandeProduit();
        demande.setNomProduit(nomProduit);
        demande.setMessage(message);

        // Enregistrement
        DemandeProduit nouvelleDemande = demandeService.enregistrerDemande(demande);

        // Envoi email à l'admin
        emailService.envoyerMailAdmin(nouvelleDemande.getId(),
                nomProduit,
                message);

        redirectAttributes.addFlashAttribute("success",
                "Votre demande a été envoyée à l’administrateur !");
        return "redirect:/produits";
    }

    // ✅ Liste des demandes pour l'admin
    @GetMapping("/admin/liste")
    @Secured("ROLE_ADMIN")
    public String listeDemandes(Model model) {
        List<DemandeProduit> demandes = demandeService.findAll();
        model.addAttribute("demandes", demandes);
        return "demandes/demandes"; // chemin vers ton HTML
    }

    // ✅ L'admin clique sur "Accepter"
    @GetMapping("/admin/accepter/{id}")
    @Secured("ROLE_ADMIN")
    public String accepterDemande(@PathVariable Long id,
                                  RedirectAttributes redirectAttributes) {
        demandeService.accepterDemande(id);
        redirectAttributes.addFlashAttribute("success", "La demande a été acceptée !");
        return "redirect:/produits/demande/admin/liste";
    }

    // ❌ L'admin clique sur "Refuser"
    @GetMapping("/admin/refuser/{id}")
    @Secured("ROLE_ADMIN")
    public String refuserDemande(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        demandeService.refuserDemande(id);
        redirectAttributes.addFlashAttribute("info", "La demande a été refusée.");
        return "redirect:/produits/demande/admin/liste";
    }
}
