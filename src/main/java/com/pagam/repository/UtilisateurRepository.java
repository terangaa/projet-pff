package com.pagam.repository;

import com.pagam.entity.Role;
import com.pagam.entity.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);
    List<Utilisateur> findByRole(Role role);

    // Compter par rôle
    long countByRole(Role role);

    Optional<Utilisateur> findByResetToken(String resetToken);

    Page<Utilisateur> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String nom, String prenom, String email, Pageable pageable);


    // Pour le total des utilisateurs, utilisez count() fourni par JpaRepository
    // Pas besoin de définir countAllUsers()
}
