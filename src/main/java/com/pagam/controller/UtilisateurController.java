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
public class UtilisateurController {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurController(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    // Liste des utilisateurs
    @GetMapping
    public String listeUtilisateurs(Model model) {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        model.addAttribute("utilisateurs", utilisateurs);
        return "utilisateurs/utilisateur"; // fichier template
    }

    // Formulaire création utilisateur
    @GetMapping("/creer")
    public String creerUtilisateurForm(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        return "utilisateurs/creer-utilisateur";
    }

    @PostMapping("/creer")
    public String creerUtilisateur(@ModelAttribute Utilisateur utilisateur) {
        utilisateurRepository.save(utilisateur);
        return "redirect:/utilisateurs";
    }

    // Formulaire modification utilisateur
    @GetMapping("/modifier/{id}")
    public String modifierUtilisateurForm(@PathVariable Long id, Model model) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé : " + id));
        model.addAttribute("utilisateur", utilisateur);
        return "utilisateurs/modifier-utilisateur";
    }

    @PostMapping("/modifier/{id}")
    public String modifierUtilisateur(@PathVariable Long id,
                                      @ModelAttribute Utilisateur utilisateur) {
        utilisateur.setId(id);
        utilisateurRepository.save(utilisateur);
        return "redirect:/utilisateurs";
    }

    // Supprimer utilisateur
    @GetMapping("/supprimer/{id}")
    public String supprimerUtilisateur(@PathVariable Long id) {
        utilisateurRepository.deleteById(id);
        return "redirect:/utilisateurs";
    }

    // Détail utilisateur
    @GetMapping("/detail/{id}")
    public String detailUtilisateur(@PathVariable Long id, Model model) {
        Optional<Utilisateur> opt = utilisateurRepository.findById(id);
        if (opt.isEmpty()) return "redirect:/utilisateurs";
        model.addAttribute("utilisateur", opt.get());
        return "utilisateurs/detail-utilisateur";
    }
}
