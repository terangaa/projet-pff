package com.pagam.repository;

import com.pagam.entity.Producteur;
import com.pagam.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import com.pagam.entity.Vente;
import com.pagam.entity.Commande;
import java.util.List;

public interface VenteRepository extends JpaRepository<Vente, Long> {
    // Récupérer toutes les ventes d'une commande spécifique
    List<Vente> findByCommande(Commande commande);

    List<Vente> findByProduit_Producteur(Producteur producteur);

    List<Vente> findByProduit(Produit produit);

    boolean existsByCommande(Commande commande);
}

