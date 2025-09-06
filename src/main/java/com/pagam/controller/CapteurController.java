package com.pagam.controller;

import com.pagam.entity.Capteur;
import com.pagam.entity.Mesure;
import com.pagam.service.CapteurService;
import com.pagam.service.MesureService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/capteurs")
public class CapteurController {

    private final CapteurService capteurService;
    private final MesureService mesureService;

    public CapteurController(CapteurService capteurService, MesureService mesureService) {
        this.capteurService = capteurService;
        this.mesureService = mesureService;
    }

    // 1️⃣ Liste des capteurs
    @GetMapping
    public String listeCapteurs(Model model) {
        List<Capteur> capteurs = capteurService.tousLesCapteurs();

        // Ajouter les icônes selon le type
        for (Capteur capteur : capteurs) {
            switch (capteur.getType()) {
                case "TEMP" -> capteur.setIconClass("bi bi-thermometer-half");
                case "HUMIDITE" -> capteur.setIconClass("bi bi-droplet");
                case "SOL" -> capteur.setIconClass("bi bi-leaf");
                default -> capteur.setIconClass("bi bi-question");
            }
        }

        // Calcul de la moyenne des températures
        double moyenneTemp = capteurs.stream()
                .filter(c -> "TEMP".equals(c.getType()))
                .flatMap(c -> c.getMesures().stream())
                .mapToDouble(Mesure::getValeur)
                .average()
                .orElse(0.0);

        model.addAttribute("capteurs", capteurs);
        model.addAttribute("moyenneTemp", moyenneTemp);
        return "capteurs/capteur";
    }

    // 2️⃣ Formulaire ajout
    @GetMapping("/ajouter")
    public String formAjouterCapteur(Model model){
        model.addAttribute("capteur", new Capteur());
        return "capteurs/ajouter-capteur";
    }

    // 3️⃣ Traitement ajout
    @PostMapping("/ajouter")
    public String ajouterCapteur(@ModelAttribute Capteur capteur){
        capteurService.save(capteur);
        return "redirect:/capteurs";
    }

    // 4️⃣ Formulaire modification
    @GetMapping("/modifier/{id}")
    public String formModifierCapteur(@PathVariable Long id, Model model){
        Capteur capteur = capteurService.getById(id);
        model.addAttribute("capteur", capteur);
        return "capteurs/modifier-capteur";
    }

    // 5️⃣ Traitement modification
    @PostMapping("/modifier/{id}")
    public String modifierCapteur(@PathVariable Long id, @ModelAttribute Capteur capteur){
        capteur.setId(id);
        capteurService.save(capteur);
        return "redirect:/capteurs";
    }

    // 6️⃣ Suppression
    @GetMapping("/supprimer/{id}")
    public String supprimerCapteur(@PathVariable Long id){
        capteurService.deleteById(id);
        return "redirect:/capteurs";
    }

    // 7️⃣ Détail d’un capteur
    @GetMapping("/detail/{id}")
    public String detailCapteur(@PathVariable Long id, Model model) {
        Capteur capteur = capteurService.getById(id);

        // Récupérer les dernières mesures et la moyenne
        List<Mesure> mesures = mesureService.getDernieresMesures(capteur.getId());
        Double moyenne = mesureService.calculerMoyenne(capteur);

        // Ajouter l’icône
        switch (capteur.getType()) {
            case "TEMP" -> capteur.setIconClass("bi bi-thermometer-half");
            case "HUMIDITE" -> capteur.setIconClass("bi bi-droplet");
            case "SOL" -> capteur.setIconClass("bi bi-leaf");
            default -> capteur.setIconClass("bi bi-question");
        }

        model.addAttribute("capteur", capteur);
        model.addAttribute("mesures", mesures);
        model.addAttribute("moyenne", moyenne);

        return "capteurs/detail-capteur";
    }
}
