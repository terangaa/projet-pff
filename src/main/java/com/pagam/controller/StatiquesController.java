package com.pagam.controller;

import com.pagam.entity.Role;
import com.pagam.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StatiquesController {

    @Autowired
    private  UtilisateurRepository utilisateurRepository;

    public StatiquesController(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @GetMapping("/statistiques")
    public Map<String, Integer> getStatistiques() {
        int nbAdmin = Math.toIntExact(utilisateurRepository.countByRole(Role.ADMIN));
        int nbAgriculteur = Math.toIntExact(utilisateurRepository.countByRole(Role.AGRICULTEUR));
        int nbAcheteur = Math.toIntExact(utilisateurRepository.countByRole(Role.ACHETEUR));
        int totalUsers = Math.toIntExact(utilisateurRepository.count());

        Map<String, Integer> stats = new HashMap<>();
        stats.put("nbAdmin", nbAdmin);
        stats.put("nbAgriculteur", nbAgriculteur);
        stats.put("nbAcheteur", nbAcheteur);
        stats.put("totalUsers", totalUsers);

        return stats;
    }
}
