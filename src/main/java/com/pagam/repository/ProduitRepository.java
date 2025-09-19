package com.pagam.repository;

import com.pagam.entity.Produit;
import com.pagam.entity.Producteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    List<Produit> findByProducteur(Producteur producteur);
}
