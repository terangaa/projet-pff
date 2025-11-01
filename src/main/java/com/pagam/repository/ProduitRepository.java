package com.pagam.repository;

import com.pagam.entity.Producteur;
import com.pagam.entity.Produit;
import com.pagam.entity.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    // 🔹 Trouver un produit par nom (insensible à la casse)
    Produit findByNomIgnoreCase(String nom);

    // 🔹 Trouver les produits appartenant à un agriculteur donné
    List<Produit> findByAgriculteur(Producteur agriculteur);

    // 🔹 Trouver les produits disponibles (quantité > 0)
    List<Produit> findByQuantiteGreaterThan(int quantite);

    // 🔹 Pagination complète des produits
    Page<Produit> findAll(Pageable pageable);

    // 🔹 Recherche avec pagination (par nom ou description)
    @Query("SELECT p FROM Produit p WHERE LOWER(p.nom) LIKE LOWER(CONCAT('%', :motCle, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :motCle, '%'))")
    Page<Produit> rechercherProduits(String motCle, Pageable pageable);

    // 🔹 Produits d’un producteur avec pagination
    Page<Produit> findByAgriculteur(Producteur agriculteur, Pageable pageable);

    // 🔹 Produits triés par prix croissant
    List<Produit> findAllByOrderByPrixAsc();

    // 🔹 Produits triés par prix décroissant
    List<Produit> findAllByOrderByPrixDesc();

    // 🔹 Compter le nombre de produits d’un producteur
    long countByAgriculteur(Producteur agriculteur);

    // 🔹 Compter le nombre de produits disponibles
    long countByQuantiteGreaterThan(int quantite);

    Page<Produit> findByNomContainingIgnoreCase(String trim, Pageable pageable);

    Optional<Produit> findByNomAndAgriculteur(String nom, Producteur producteur);

    List<Produit> findByAgriculteurIsNotNull();
}
    