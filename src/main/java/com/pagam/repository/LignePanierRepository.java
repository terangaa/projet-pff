package com.pagam.repository;

import com.pagam.entity.LignePanier;
import com.pagam.entity.Panier;
import com.pagam.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LignePanierRepository extends JpaRepository<LignePanier, Long> {
    List<LignePanier> findByPanier(Panier panier);

    LignePanier findByPanierAndProduit(Panier panier, Produit produit);
}
