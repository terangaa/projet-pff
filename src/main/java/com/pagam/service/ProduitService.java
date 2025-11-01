package com.pagam.service;

import com.pagam.entity.Producteur;
import com.pagam.entity.Produit;
import com.pagam.entity.Utilisateur;
import com.pagam.repository.CommandeRepository;
import com.pagam.repository.ProducteurRepository;
import com.pagam.repository.ProduitRepository;
import com.pagam.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CommandeRepository commandeRepository;
    private final ProducteurRepository producteurRepository;
    private final StockageService stockageService;

    public ProduitService(ProduitRepository produitRepository, UtilisateurRepository utilisateurRepository, CommandeRepository commandeRepository, ProducteurRepository producteurRepository, StockageService stockageService) {
        this.produitRepository = produitRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.commandeRepository = commandeRepository;
        this.producteurRepository = producteurRepository;
        this.stockageService = stockageService;
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
        produit.setDescription(produitDetails.getDescription());
        produit.setAgriculteur(produitDetails.getAgriculteur());

        return produitRepository.save(produit);
    }

    // 📌 Supprimer un produit en vérifiant les contraintes
    public boolean deleteProduit(Long id) {
        Produit produit = findById(id);
        if (produit == null) {
            return false; // produit inexistant
        }

        boolean existeDansCommande = commandeRepository.existsByProduit(produit);
        if (existeDansCommande) {
            return false; // impossible de supprimer
        }

        produitRepository.delete(produit);
        return true;
    }

    // 📌 Trouver un produit par nom (insensible à la casse)
    // 📌 Trouver un produit par nom (insensible à la casse)
    public Produit findByNomIgnoreCase(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return null; // ou tu peux lever une exception si tu veux le signaler
        }
        return produitRepository.findByNomIgnoreCase(nom.trim());
    }


    // 📌 Produits d’un producteur
    public List<Produit> getProduitsParAgriculteur(Producteur producteur) {
        return produitRepository.findByAgriculteur(producteur);
    }


    // ✅ Méthode pour trouver tous les produits
    public List<Produit> findAll() {
        return produitRepository.findAll();
    }

    // ✅ Trouver un produit par ID (avec Optional)
    public Optional<Produit> findByIdOptional(Long id) {
        return produitRepository.findById(id);
    }

    // ✅ Trouver un produit par nom (sans tenir compte de la casse)

    // ✅ Sauvegarder un produit
    public Produit save(Produit produit) {
        return produitRepository.save(produit);
    }

    // ✅ Supprimer un produit
    public void deleteById(Long id) {
        produitRepository.deleteById(id);
    }

    // ✅ Méthode pour récupérer uniquement les produits disponibles
    public List<Produit> findProduitsDisponibles() {
        return produitRepository.findByQuantiteGreaterThan(0);
    }

    public Produit getProduitById(Long id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé avec l'ID : " + id));
    }


    // AjouterProduit
    // AjouterProduit
    public void ajouterProduit(Produit produit, MultipartFile imageFile) {
        try {
            // ✅ Gestion de l'image (sauvegarde + chemin relatif)
            if (imageFile != null && !imageFile.isEmpty()) {
                // Préciser le dossier "produits"
                String cheminImage = stockageService.save(imageFile, "produits");
                produit.setImage(cheminImage);
            }

            // ✅ Valeur par défaut pour le prix
            if (produit.getPrix() == null) {
                produit.setPrix(0.0);
            }

            // ✅ Valeur par défaut pour la quantité (optionnel)
            if (produit.getQuantite() == null) {
                produit.setQuantite(0);
            }

            // ✅ Sauvegarde en base
            produitRepository.save(produit);

            System.out.println("✅ Produit ajouté : " + produit.getNom());

        } catch (Exception e) {
            // En cas d'erreur, suppression du fichier uploadé pour éviter les fichiers orphelins
            if (produit.getImage() != null) {
                stockageService.delete(produit.getImage(), "produits");
            }
            throw new RuntimeException("❌ Erreur lors de l'ajout du produit : " + e.getMessage(), e);
        }
    }

    // AjouterUtilisateur
    public void ajouterUtilisateur(Utilisateur utilisateur, MultipartFile imageFile) {
        try {
            // ✅ Gestion de l'image (sauvegarde + chemin relatif dans le dossier "utilisateurs")
            if (imageFile != null && !imageFile.isEmpty()) {
                String cheminPhoto = stockageService.save(imageFile, "utilisateurs");
                utilisateur.setPhoto(cheminPhoto); // La propriété de l'entité Utilisateur
            }

            // ✅ Sauvegarde en base
            utilisateurRepository.save(utilisateur);

            System.out.println("✅ Utilisateur ajouté : " + utilisateur.getNom());

        } catch (Exception e) {
            // En cas d'erreur, suppression du fichier uploadé pour éviter les fichiers orphelins
            if (utilisateur.getPhoto() != null) {
                stockageService.delete(utilisateur.getPhoto(), "utilisateurs");
            }
            throw new RuntimeException("❌ Erreur lors de l'ajout de l'utilisateur : " + e.getMessage(), e);
        }
    }

    // ModifierProduit
    public void modifierProduit(Long id, Produit produitForm, Long agriculteurId) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produit introuvable avec l'ID : " + id));

        produit.setNom(produitForm.getNom());
        produit.setDescription(produitForm.getDescription());
        produit.setQuantite(produitForm.getQuantite());
        produit.setPrix(produitForm.getPrix());

        // Gestion image
        if (produitForm.getImage() != null && !produitForm.getImage().isEmpty()) {
            produit.setImage(produitForm.getImage());
        }

        // Gestion producteur
        if (agriculteurId != null) {
            Producteur prod = producteurRepository.findById(agriculteurId)
                    .orElseThrow(() -> new IllegalArgumentException("Producteur introuvable"));
            produit.setAgriculteur(prod);
        }

        produitRepository.save(produit);
    }

    public void delete(Produit produit) {
        produitRepository.delete(produit);
    }
}
