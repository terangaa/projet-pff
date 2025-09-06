package com.pagam.service;

import com.pagam.entity.Utilisateur;
import com.pagam.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
    }
