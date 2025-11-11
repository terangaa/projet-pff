package com.pagam.service;

import com.pagam.entity.*;
import com.pagam.repository.CommandeRepository;
import com.pagam.repository.ProduitRepository;
import com.pagam.repository.VenteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final ProduitRepository produitRepository; // ⚡ pour gérer le stock
    private final VenteRepository venteRepository;
    private final ProduitService produitService;
    private final UtilisateurService utilisateurService;

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

        // Calculer les prix
        double prixUnitaire = produit.getPrix();
        double prixTotal = prixUnitaire * commande.getQuantite();
        commande.setPrixUnitaire(prixUnitaire);  // ⚡ Ajouter cette ligne
        commande.setPrixTotal(prixTotal);

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

    @Transactional
    public Vente creerVenteDepuisCommande(Commande commande) {
        if (commande == null || commande.getId() == null)
            throw new IllegalArgumentException("La commande doit être persistée.");

        Optional<Vente> existingVente = venteRepository.findByCommande(commande);
        if (existingVente.isPresent())
            return existingVente.get();

        if (commande.getProduit() == null || commande.getAcheteur() == null)
            throw new IllegalArgumentException("Commande invalide : produit ou acheteur manquant.");

        Vente vente = new Vente();
        vente.setProduit(produitService.findById(commande.getProduit().getId()));
        vente.setAcheteur(utilisateurService.findById(commande.getAcheteur().getId()));
        vente.setQuantite(commande.getQuantite() != null ? commande.getQuantite() : 0);
        vente.setPrix(vente.getProduit().getPrix());
        vente.setCommande(commande);
        vente.setDateVente(LocalDateTime.now());
        vente.getMontantTotal();

        Vente savedVente = venteRepository.save(vente);
        commande.setVente(savedVente);

        return savedVente;
    }

    public Commande findById(Long id) {
        return commandeRepository.findById(id).orElse(null);
    }

    // Valider une commande
    public Commande validerCommande(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande introuvable"));

        if (!StatutCommande.VALIDEE.equals(commande.getStatut())) {
            Produit produit = commande.getProduit();
            if (produit.getStock() < commande.getQuantite()) {
                throw new RuntimeException("Stock insuffisant pour " + produit.getNom());
            }
            produit.setStock(produit.getStock() - commande.getQuantite());
            produitRepository.save(produit);

            commande.setStatut(StatutCommande.VALIDEE);
            commandeRepository.save(commande);
        }

        return commande;
    }

    // Annuler une commande
    public Commande annulerCommande(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande introuvable"));

        if (!StatutCommande.ANNULEE.equals(commande.getStatut())) {
            if (StatutCommande.VALIDEE.equals(commande.getStatut())) {
                Produit produit = commande.getProduit();
                produit.setStock(produit.getStock() + commande.getQuantite());
                produitRepository.save(produit);
            }

            commande.setStatut(StatutCommande.ANNULEE);
            commandeRepository.save(commande);
        }

        return commande;
    }

    // Supprimer une commande
    public void supprimerCommande(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande introuvable"));

        if (StatutCommande.VALIDEE.equals(commande.getStatut())) {
            Produit produit = commande.getProduit();
            produit.setStock(produit.getStock() + commande.getQuantite());
            produitRepository.save(produit);
        }

        commandeRepository.delete(commande);
    }

}
