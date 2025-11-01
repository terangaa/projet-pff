package com.pagam.service;

import com.pagam.entity.Producteur;
import com.pagam.entity.Produit;
import com.pagam.repository.ProducteurRepository;
import com.pagam.repository.ProduitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProducteurService {

    private final ProducteurRepository producteurRepository;
    private final ProduitRepository produitRepository;

    public ProducteurService(ProducteurRepository producteurRepository, ProduitRepository produitRepository) {
        this.producteurRepository = producteurRepository;
        this.produitRepository = produitRepository;
    }

    public Producteur saveProducteur(Producteur producteur) {
        return producteurRepository.save(producteur);
    }

    public List<Producteur> getAllProducteurs() {
        return producteurRepository.findAll();
    }

    public Producteur getProducteurById(Long id) {
        return producteurRepository.findById(id).orElse(null);
    }

    public void deleteProducteur(Long id) {
        producteurRepository.deleteById(id);
    }


    public List<Produit> findAll() {
        return produitRepository.findAll();
    }

    public Optional<Producteur> findById(Long agriculteurId) {
        return producteurRepository.findById(agriculteurId);
    }

    // ✅ Récupérer le producteur par le nom d'utilisateur
    public Producteur findByUtilisateurEmail(String email) {
        return (Producteur) producteurRepository.findByUtilisateurEmail(email).orElse(null);
    }
}
