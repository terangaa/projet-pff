package com.pagam.repository;

import com.pagam.entity.Commande;
import com.pagam.entity.Produit;
import com.pagam.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {

    // Vérifie si une commande utilise ce produit
    boolean existsByProduit(Produit produit);

    // ✅ Récupérer toutes les commandes d’un acheteur
    List<Commande> findByAcheteur(Utilisateur acheteur);
}
