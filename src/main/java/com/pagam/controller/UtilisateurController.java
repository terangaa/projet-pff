package com.pagam.controller;

import com.pagam.entity.Utilisateur;
import com.pagam.repository.UtilisateurRepository;
import com.pagam.service.StockageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Controller
@RequestMapping("/utilisateurs")
public class UtilisateurController {

    private final UtilisateurRepository utilisateurRepository;
    private final StockageService stockageService;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurController(UtilisateurRepository utilisateurRepository,
                                 StockageService stockageService,
                                 PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.stockageService = stockageService;
        this.passwordEncoder = passwordEncoder;
    }

    // Liste des utilisateurs avec pagination et recherche
    @GetMapping
    public String listUtilisateurs(@RequestParam(value = "motCle", required = false) String motCle,
                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                   Model model) {

        Pageable pageable = PageRequest.of(page, 12, Sort.by("nom").ascending());
        Page<Utilisateur> utilisateursPage;

        if (motCle != null && !motCle.isEmpty()) {
            utilisateursPage = utilisateurRepository
                    .findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            motCle, motCle, motCle, pageable
                    );
        } else {
            utilisateursPage = utilisateurRepository.findAll(pageable);
        }

        model.addAttribute("utilisateursPage", utilisateursPage);
        model.addAttribute("motCle", motCle);

        return "utilisateurs/utilisateur";
    }

    // Formulaire création utilisateur
    @GetMapping("/creer")
    public String creerUtilisateurForm(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        return "utilisateurs/creer-utilisateur";
    }

    // Enregistrement utilisateur
    @PostMapping("/creer")
    public String enregistrerUtilisateur(@ModelAttribute Utilisateur utilisateur,
                                         @RequestParam(value = "photoFile", required = false) MultipartFile photoFile) {

        if (utilisateur.getMotDePasse() != null && !utilisateur.getMotDePasse().isEmpty()) {
            utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        }

        if (photoFile != null && !photoFile.isEmpty()) {
            String fileName = stockageService.save(photoFile, "utilisateurs");
            utilisateur.setPhoto(fileName);
        }

        utilisateurRepository.save(utilisateur);
        return "redirect:/utilisateurs";
    }

    // Formulaire modification utilisateur
    @GetMapping("/modifier/{id}")
    public String modifierUtilisateurForm(@PathVariable Long id, Model model) {
        Optional<Utilisateur> optUser = utilisateurRepository.findById(id);
        if (optUser.isEmpty()) return "redirect:/utilisateurs";

        model.addAttribute("utilisateur", optUser.get());
        return "utilisateurs/modifier-utilisateur";
    }

    // Enregistrement modification utilisateur
    @PostMapping("/modifier/{id}")
    public String enregistrerModification(@PathVariable Long id,
                                          @ModelAttribute Utilisateur utilisateur,
                                          @RequestParam(value = "photoFile", required = false) MultipartFile photoFile) {

        Optional<Utilisateur> optUser = utilisateurRepository.findById(id);
        if (optUser.isEmpty()) return "redirect:/utilisateurs";

        Utilisateur existingUser = optUser.get();
        existingUser.setNom(utilisateur.getNom());
        existingUser.setPrenom(utilisateur.getPrenom());
        existingUser.setEmail(utilisateur.getEmail());
        existingUser.setRole(utilisateur.getRole());

        if (utilisateur.getMotDePasse() != null && !utilisateur.getMotDePasse().isEmpty()) {
            existingUser.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        }

        if (photoFile != null && !photoFile.isEmpty()) {
            if (existingUser.getPhoto() != null && !existingUser.getPhoto().isEmpty()) {
                stockageService.delete(existingUser.getPhoto(), "utilisateurs");
            }
            String fileName = stockageService.save(photoFile, "utilisateurs");
            existingUser.setPhoto(fileName);
        }

        utilisateurRepository.save(existingUser);
        return "redirect:/utilisateurs";
    }

    // Suppression utilisateur
    @GetMapping("/supprimer/{id}")
    public String supprimerUtilisateur(@PathVariable Long id) {
        Optional<Utilisateur> optUser = utilisateurRepository.findById(id);
        if (optUser.isPresent()) {
            Utilisateur user = optUser.get();
            if (user.getPhoto() != null && !user.getPhoto().isEmpty()) {
                stockageService.delete(user.getPhoto(), "utilisateurs");
            }
            utilisateurRepository.deleteById(id);
        }
        return "redirect:/utilisateurs";
    }

    // Upload/modification photo AJAX
    @PostMapping("/upload-photo/{id}")
    @ResponseBody
    public ResponseEntity<String> uploadPhoto(@PathVariable Long id,
                                              @RequestParam("photo") MultipartFile photo) {

        Optional<Utilisateur> optUser = utilisateurRepository.findById(id);
        if (optUser.isEmpty()) return ResponseEntity.badRequest().body("/images/default-avatar.png");

        Utilisateur user = optUser.get();
        if (user.getPhoto() != null && !user.getPhoto().isEmpty()) {
            stockageService.delete(user.getPhoto(), "utilisateurs");
        }

        String fileName = stockageService.save(photo, "utilisateurs");
        if (fileName != null) {
            user.setPhoto(fileName);
            utilisateurRepository.save(user);
            return ResponseEntity.ok(fileName);
        } else {
            return ResponseEntity.status(500).body("/images/default-avatar.png");
        }
    }
}
