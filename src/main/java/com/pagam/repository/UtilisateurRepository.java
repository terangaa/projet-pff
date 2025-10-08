package com.pagam.repository;

import com.pagam.entity.Role;
import com.pagam.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    // Compter par rôle
    long countByRole(Role role);


    // Pour le total des utilisateurs, utilisez count() fourni par JpaRepository
    // Pas besoin de définir countAllUsers()
}
