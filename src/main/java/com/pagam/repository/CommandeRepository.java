package com.pagam.repository;

import com.pagam.entity.Commande;
import com.pagam.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandeRepository extends JpaRepository<Commande, Long> {

    // Vérifie si une commande utilise ce produit
    boolean existsByProduit(Produit produit);
}
