package com.pagam.service;

import com.pagam.entity.Producteur;
import com.pagam.entity.Produit;
import com.pagam.repository.ProduitRepository;
import com.pagam.repository.CommandeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final CommandeRepository commandeRepository;

    public ProduitService(ProduitRepository produitRepository, CommandeRepository commandeRepository) {
        this.produitRepository = produitRepository;
        this.commandeRepository = commandeRepository;
    }

    // 📌 Liste de tous les produits
    public List<Produit> getAllProduits() {
        return produitRepository.findAll();
    }

    // 📌 Trouver un produit par ID
    public Produit findById(Long id) {
        Optional<Produit> produitOpt = produitRepository.findById(id);
        return produitOpt.orElse(null);
    }

    // 📌 Enregistrer un produit
    public Produit saveProduit(Produit produit) {
        return produitRepository.save(produit);
    }

    // 📌 Mettre à jour un produit
    public Produit updateProduit(Long id, Produit produitDetails) {
        Produit produit = findById(id);
        if (produit == null) return null;

        produit.setNom(produitDetails.getNom());
        produit.setPrix(produitDetails.getPrix());
        produit.setStock(produitDetails.getStock());
        produit.setProducteur(produitDetails.getProducteur());

        return produitRepository.save(produit);
    }


    // 📌 Supprimer un produit en vérifiant les contraintes
    public boolean deleteProduit(Long id) {
        Produit produit = findById(id);

        if (produit == null) {
            return false; // produit inexistant
        }

        // Vérifier si ce produit est lié à une commande
        boolean existeDansCommande = commandeRepository.existsByProduit(produit);

        if (existeDansCommande) {
            return false; // impossible de supprimer
        }

        produitRepository.delete(produit);
        return true; // suppression réussie
    }

    public Produit getProduitById(Long id) {
        Optional<Produit> produit = produitRepository.findById(id);
        return produit.orElse(null); // ou lancer une exception si non trouvé
    }

    public List<Produit> findAll() {
        return produitRepository.findAll();
    }

    public List<Produit> getProduitsParAgriculteur(Producteur producteur) {
        return produitRepository.findByProducteur(producteur);
    }

    public void save(Produit produit) {
        produitRepository.save(produit);
    }
}
