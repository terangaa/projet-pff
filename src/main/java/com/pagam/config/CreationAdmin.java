package com.pagam.config;

import com.pagam.entity.Role;
import com.pagam.entity.Utilisateur;
import com.pagam.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreationAdmin implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Vérifie s’il y a déjà des utilisateurs pour éviter de dupliquer
        if (utilisateurRepository.count() == 0) {
            System.out.println("⚙️ Initialisation des administrateurs...");

            // 🔐 Création des 4 administrateurs
            createAdmin("Yade", "Sadikh", "sadikhyade851@gmail.com", "sadikh123");
            createAdmin("Mbaye", "Inssa", "insam621.com@gmail.com", "inssa123");

            System.out.println("✅ Administrateurs créés avec succès !");
        } else {
            System.out.println("ℹ️ Données déjà initialisées, aucune création nécessaire.");
        }
    }

    private void createAdmin(String nom, String prenom, String email, String password) {
        Utilisateur admin = Utilisateur.builder()
                .nom(nom)
                .prenom(prenom)
                .email(email)
                .motDePasse(passwordEncoder.encode(password))
                .role(Role.ADMIN)
                .build();

        utilisateurRepository.save(admin);
    }
}
