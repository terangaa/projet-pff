package com.pagam.repository;

import com.pagam.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProduitRepository extends JpaRepository<Produit, Long> {
    List<Produit> findByProducteurId(Long producteurId);

}
