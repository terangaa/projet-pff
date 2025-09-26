package com.pagam.service;

import com.pagam.entity.Commande;
import com.pagam.entity.Produit;
import com.pagam.entity.Utilisateur;
import com.pagam.entity.Vente;
import com.pagam.repository.CommandeRepository;
import com.pagam.repository.ProduitRepository;
import com.pagam.repository.VenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final ProduitRepository produitRepository; // ⚡ pour gérer le stock
    private final VenteRepository venteRepository;

    // ✅ Créer une commande avec contrôle du stock
    public Commande saveCommande(Commande commande) {
        Produit produit = produitRepository.findById(commande.getProduit().getId())
                .orElseThrow(() -> new RuntimeException("Produit introuvable"));

        // Vérifier le stock disponible
        if (commande.getQuantite() > produit.getStock()) {
            throw new RuntimeException("Stock insuffisant pour le produit : " + produit.getNom());
        }

        // Décrémenter le stock
        produit.setStock(produit.getStock() - commande.getQuantite());
        produitRepository.save(produit);

        // Calculer le prix total
        commande.setPrixTotal(produit.getPrix() * commande.getQuantite());

        // Ajouter la date de commande
        commande.setDateCommande(LocalDateTime.now());

        // Sauvegarder la commande
        return commandeRepository.save(commande);
    }

    // Toutes les commandes (admin)
    public List<Commande> getAllCommandes() {
        List<Commande> commandes = commandeRepository.findAll();
        for (Commande c : commandes) {
            if (c.getProduit() != null && c.getQuantite() != null) {
                c.setPrixTotal(c.getProduit().getPrix() * c.getQuantite());
            } else {
                c.setPrixTotal(0.0);
            }
        }
        return commandes;
    }

    // Une commande par ID
    public Commande getCommandeById(Long id) {
        return commandeRepository.findById(id).orElse(null);
    }

    // Supprimer une commande
    public void deleteCommande(Long id) {
        commandeRepository.deleteById(id);
    }

    // ✅ Récupérer les commandes d’un acheteur spécifique
    public List<Commande> getCommandesByAcheteur(Utilisateur acheteur) {
        List<Commande> commandes = commandeRepository.findByAcheteur(acheteur);
        for (Commande c : commandes) {
            if (c.getProduit() != null && c.getQuantite() != null) {
                c.setPrixTotal(c.getProduit().getPrix() * c.getQuantite());
            } else {
                c.setPrixTotal(0.0);
            }
        }
        return commandes;
    }

    public void save(Commande commande) {
        commandeRepository.save(commande);
    }

    public Vente creerVenteDepuisCommande(Commande commande) {
        if (commande.getVente() != null) {
            throw new RuntimeException("Cette commande a déjà été transformée en vente");
        }

        Vente vente = Vente.builder()
                .commande(commande)
                .montant(commande.getPrixTotal())
                .dateVente(LocalDateTime.now())
                .build();

        // Lier la vente à la commande
        commande.setVente(vente);
        commandeRepository.save(commande); // sauvegarde la relation

        return venteRepository.save(vente);
    }

    public Commande findById(Long id) {
        return commandeRepository.findById(id).orElse(null);
    }

}
