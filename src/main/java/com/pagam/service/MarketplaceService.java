package com.pagam.service;

import com.pagam.entity.StatutCommande;
import com.pagam.entity.Utilisateur;
import com.pagam.entity.Produit;
import com.pagam.entity.Commande;
import com.pagam.repository.UtilisateurRepository;
import com.pagam.repository.ProduitRepository;
import com.pagam.repository.CommandeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketplaceService {

    private final UtilisateurRepository utilisateurRepository;
    private final ProduitRepository produitRepository;
    private final CommandeRepository commandeRepository;

    // Ajouter un produit
    public Produit ajouterProduit(Produit produit, Long producteurId) {
        Utilisateur producteur = utilisateurRepository.findById(producteurId)
                .orElseThrow(() -> new RuntimeException("Producteur introuvable"));
        // tu peux lier le produit au producteur si besoin
        return produitRepository.save(produit);
    }

    // Lister tous les produits
    public List<Produit> listerProduits() {
        return produitRepository.findAll();
    }

    // Passer une commande
    public Commande passerCommande(Long acheteurId, Long produitId, int quantite) {
        Utilisateur acheteur = utilisateurRepository.findById(acheteurId)
                .orElseThrow(() -> new RuntimeException("Acheteur introuvable"));
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        // Déclaration et initialisation de la variable commande
        Commande commande = new Commande();
        commande.setAcheteur(acheteur);
        commande.setProduit(produit);
        commande.setQuantite(quantite);
        commande.setPrixTotal(produit.getPrix() * quantite); // si produit a un champ prix
        commande.setDateCommande(LocalDateTime.now());
        commande.setStatut(StatutCommande.EN_COURS);


        return commandeRepository.save(commande);
    }

    // Lister les commandes d’un acheteur
    public List<Commande> listerCommandesAcheteur(Long acheteurId) {
        Utilisateur acheteur = utilisateurRepository.findById(acheteurId)
                .orElseThrow(() -> new RuntimeException("Acheteur introuvable"));
        return commandeRepository.findAll().stream()
                .filter(c -> c.getAcheteur().getId().equals(acheteurId))
                .toList();
    }
}
