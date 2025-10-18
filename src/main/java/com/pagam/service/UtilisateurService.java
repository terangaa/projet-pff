package com.pagam.service;

import com.pagam.entity.Utilisateur;
import com.pagam.repository.UtilisateurRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {
        private final UtilisateurRepository utilisateurRepository;

        public UtilisateurService(UtilisateurRepository utilisateurRepository) {
            this.utilisateurRepository = utilisateurRepository;
        }
        public Utilisateur saveUtilisateur(Utilisateur utilisateur) {
            return utilisateurRepository.save(utilisateur);
        }
        public List<Utilisateur> getAllUtilisateur() {
            return utilisateurRepository.findAll();
        }
    // Méthode à ajouter
        public List<Utilisateur> getAllUtilisateurs() {
            return utilisateurRepository.findAll();
        }
        public Utilisateur getUtilisateurById(Long id) {
            return utilisateurRepository.findById(id).orElse(null);
        }
        public void deleteUtilisateur(Long id) {
            utilisateurRepository.deleteById(id);
        }

    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur getByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElse(null); // retourne null si l'utilisateur n'existe pas
    }

    // ✅ Récupérer un utilisateur par email
    // Récupérer un utilisateur par email
    public Utilisateur getUtilisateurByEmail(String email) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);
        return utilisateurOpt.orElseThrow(() ->
                new RuntimeException("Utilisateur introuvable avec l'email : " + email)
        );
    }

    public Utilisateur utilisateurConnecte() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName().equals("anonymousUser")) {
            return null; // pas connecté
        }
        String email = auth.getName(); // normalement l'email ou username
        return utilisateurRepository.findByEmail(email)
                .orElse(null); // ou lancer exception si pas trouvé
    }

    public Utilisateur findById(Long acheteurId) {
        return utilisateurRepository.findById(acheteurId)
                .orElse(null); // retourne null si aucun utilisateur trouvé
    }

    // Méthode pour récupérer un utilisateur par ID en Optional
    public Optional<Utilisateur> findByIdOptional(Long id) {
        return utilisateurRepository.findById(id);
    }

    public void updatePassword(Utilisateur utilisateur, String newPassword) {
    }

    public Utilisateur findByResetToken(String token) {
        return utilisateurRepository.findByResetToken(token).orElse(null);
    }

    public Utilisateur findByEmail(String email) {
        return utilisateurRepository.findByEmail(email).orElse(null);
    }

    public Utilisateur save(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    public boolean existsByEmail(String email) {
        return utilisateurRepository.findByEmail(email).isPresent();
    }
}
