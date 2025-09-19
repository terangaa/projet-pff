package com.pagam.repository;

import com.pagam.entity.Producteur;
import com.pagam.entity.Produit;
import com.pagam.entity.Vente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VenteRepository extends JpaRepository<Vente, Long> {

    List<Vente> findByProduit_Producteur(Producteur producteur);

    List<Vente> findByProduit(Produit produit);
}
