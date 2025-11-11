package com.pagam.repository;

import com.pagam.entity.Producteur;
import com.pagam.entity.Produit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import com.pagam.entity.Vente;
import com.pagam.entity.Commande;
import java.util.List;
import java.util.Optional;

public interface VenteRepository extends JpaRepository<Vente, Long> {
    // Récupérer toutes les ventes d'une commande spécifique
    Optional<Vente> findByCommande(Commande commande);

    List<Vente> findByProduit_Agriculteur(Producteur agriculteur);

    List<Vente> findByProduit(Produit produit);

    boolean existsByCommande(Commande commande);

        @Query("SELECT v FROM Vente v " +
                "LEFT JOIN FETCH v.produit p " +
                "LEFT JOIN FETCH v.acheteur a " +
                "LEFT JOIN FETCH v.commande c")
        List<Vente> findAllWithProduitAndAcheteur();
    }

