package com.pagam.service;

import com.pagam.entity.*;
import com.pagam.repository.CommandeRepository;
import com.pagam.repository.PanierRepository;
import com.pagam.repository.ProduitRepository;
import com.pagam.repository.UtilisateurRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PanierService {

    private final HttpSession session;
    private final PanierRepository panierRepository;
    private final ProduitRepository produitRepository;
    private final CommandeRepository commandeRepository;
    private final UtilisateurRepository utilisateurRepository;

    // Récupérer le panier de la session
    public Panier getPanierFromSession() {
        Panier panier = (Panier) session.getAttribute("panier");
        if (panier == null) {
            panier = Panier.builder()
                    .total(0.0)
                    .lignes(new ArrayList<>())
                    .build();
            session.setAttribute("panier", panier);
        }
        return panier;
    }

    // Ajouter un produit au panier
    public void ajouterProduit(Long produitId, int quantite) throws Exception {
        Produit produit = produitRepository.findById(produitId)
                .orElseThrow(() -> new Exception("Produit introuvable"));

        if (produit.getStock() < quantite) {
            throw new Exception("Stock insuffisant pour le produit: " + produit.getNom());
        }

        Panier panier = getPanierFromSession();
        LignePanier ligneExistante = panier.getItems().stream()
                .filter(ligne -> ligne.getProduit().getId().equals(produitId))
                .findFirst()
                .orElse(null);

        if (ligneExistante != null) {
            ligneExistante.setQuantite(ligneExistante.getQuantite() + quantite);
            ligneExistante.setMontantTotal(ligneExistante.getPrix() * ligneExistante.getQuantite());
        } else {
            LignePanier nouvelleLigne = LignePanier.builder()
                    .produit(produit)
                    .quantite(quantite)
                    .prix(produit.getPrix())
                    .montantTotal(produit.getPrix() * quantite)
                    .build();
            panier.getItems().add(nouvelleLigne);
        }

        // Décrémenter le stock
        produit.setStock(produit.getStock() - quantite);
        produitRepository.save(produit);

        // Calculer le total du panier
        double total = panier.getItems().stream()
                .mapToDouble(LignePanier::getMontantTotal)
                .sum();
        panier.setTotal(total);

        session.setAttribute("panier", panier);
    }

    // Modifier la quantité d’un produit dans le panier
    public void modifierQuantite(Long produitId, int quantite) throws Exception {
        Panier panier = getPanierFromSession();

        LignePanier ligne = panier.getItems().stream()
                .filter(l -> l.getProduit().getId().equals(produitId))
                .findFirst()
                .orElseThrow(() -> new Exception("Produit non trouvé dans le panier"));

        Produit produit = ligne.getProduit();

        if (quantite > ligne.getQuantite()) {
            int diff = quantite - ligne.getQuantite();
            if (produit.getStock() < diff) {
                throw new Exception("Stock insuffisant pour le produit: " + produit.getNom());
            }
            produit.setStock(produit.getStock() - diff);
        } else if (quantite < ligne.getQuantite()) {
            int diff = ligne.getQuantite() - quantite;
            produit.setStock(produit.getStock() + diff);
        }

        ligne.setQuantite(quantite);
        ligne.setMontantTotal(ligne.getPrix() * quantite);
        produitRepository.save(produit);

        panier.getItems().removeIf(l -> l.getQuantite() <= 0);

        double total = panier.getItems().stream()
                .mapToDouble(LignePanier::getMontantTotal)
                .sum();
        panier.setTotal(total);

        session.setAttribute("panier", panier);
    }

    // Supprimer un produit du panier
    public void supprimerProduit(Long produitId) {
        Panier panier = getPanierFromSession();

        LignePanier ligne = panier.getItems().stream()
                .filter(l -> l.getProduit().getId().equals(produitId))
                .findFirst()
                .orElse(null);

        if (ligne != null) {
            Produit produit = ligne.getProduit();
            produit.setStock(produit.getStock() + ligne.getQuantite());
            produitRepository.save(produit);

            panier.getItems().remove(ligne);
        }

        double total = panier.getItems().stream()
                .mapToDouble(LignePanier::getMontantTotal)
                .sum();
        panier.setTotal(total);

        session.setAttribute("panier", panier);
    }

    // Vider le panier
    public void viderPanier() {
        Panier panier = getPanierFromSession();

        // Remettre le stock pour tous les produits
        for (LignePanier ligne : panier.getItems()) {
            Produit produit = ligne.getProduit();
            produit.setStock(produit.getStock() + ligne.getQuantite());
            produitRepository.save(produit);
        }

        panier.getItems().clear();
        panier.setTotal(0.0);
        session.setAttribute("panier", panier);
    }

    // Valider le panier (créer les commandes)
    public void validerPanier(String emailUtilisateur) throws Exception {
        Panier panier = getPanierFromSession();
        if (panier.getItems().isEmpty()) {
            throw new Exception("Panier vide, impossible de valider");
        }

        Utilisateur user = utilisateurRepository.findByEmail(emailUtilisateur)
                .orElseThrow(() -> new Exception("Utilisateur introuvable"));

        int commandesCrees = 0;
        for (LignePanier ligne : panier.getItems()) {
            Commande commande = Commande.builder()
                    .dateCommande(LocalDateTime.now())
                    .produit(ligne.getProduit())
                    .quantite(ligne.getQuantite())
                    .prixUnitaire(ligne.getPrix())
                    .prixTotal(ligne.getMontantTotal())
                    .statut(StatutCommande.EN_COURS)
                    .acheteur(user)
                    .build();
            commandeRepository.save(commande);
            commandesCrees++;
        }

        System.out.println("🎉 Total: " + commandesCrees + " commande(s) créée(s)");

        // Vider le panier après validation
        panier.getItems().clear();
        panier.setTotal(0.0);
        session.setAttribute("panier", panier);
    }

    // Lister les commandes d’un utilisateur
    public List<Commande> listerCommandesUtilisateur(String email) throws Exception {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("Utilisateur introuvable"));
        return commandeRepository.findByAcheteur(user);
    }

    public String getEmailUtilisateurConnecte() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return auth.getName(); // renvoie l'email ou username
        }
        throw new RuntimeException("Utilisateur non connecté");
    }
}
