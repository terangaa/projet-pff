package com.pagam.controller;

import com.pagam.entity.Utilisateur;
import com.pagam.repository.UtilisateurRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/utilisateurs")
@CrossOrigin(origins = "*")
public class UtilisateurController {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurController(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    // === REST API ===
    @GetMapping("/api")
    @ResponseBody
    public List<Utilisateur> getTousApi() {
        return utilisateurRepository.findAll();
    }

    @PostMapping("/api")
    @ResponseBody
    public Utilisateur creerUtilisateurApi(@RequestBody Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    // === HTML / Thymeleaf ===
    @GetMapping
    public String getTousHtml(Model model) {
        model.addAttribute("utilisateurs", utilisateurRepository.findAll());
        return "utilisateurs/utilisateur"; // fichier templates/utilisateur.html
    }

    @GetMapping("/creer")
    public String creerUtilisateurForm(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        return "utilisateurs/creer-utilisateur"; // fichier creer-utilisateur.html
    }

    @PostMapping("/creer")
    public String creerUtilisateurHtml(@ModelAttribute Utilisateur utilisateur) {
        utilisateurRepository.save(utilisateur);
        return "redirect:/utilisateurs";
    }

    // === Modifier ===
    @GetMapping("/modifier/{id}")
    public String modifierUtilisateurForm(@PathVariable Long id, Model model) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé : " + id));
        model.addAttribute("utilisateur", utilisateur);
        return "utilisateurs/modifier-utilisateur"; // fichier modifier-utilisateur.html
    }

    @PostMapping("/modifier/{id}")
    public String modifierUtilisateur(@PathVariable Long id,
                                      @ModelAttribute Utilisateur utilisateur) {
        utilisateur.setId(id); // on force l’ID pour éviter la création d’un nouveau
        utilisateurRepository.save(utilisateur);
        return "redirect:/utilisateurs";
    }

    // === Supprimer ===
    @GetMapping("/supprimer/{id}")
    public String supprimerUtilisateur(@PathVariable Long id) {
        utilisateurRepository.deleteById(id);
        return "redirect:/utilisateurs";
    }

    // === Détail ===
    @GetMapping("/detail/{id}")
    public String detailUtilisateur(@PathVariable Long id, Model model) {
        Optional<Utilisateur> optionalUtilisateur = utilisateurRepository.findById(id);
        if (optionalUtilisateur.isEmpty()) {
            return "redirect:/utilisateurs"; // si non trouvé, retour à la liste
        }
        model.addAttribute("utilisateur", optionalUtilisateur.get());
        return "utilisateurs/detail-utilisateur"; // fichier detail-utilisateur.html
    }
}
