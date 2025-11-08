package com.pagam.service;

import com.pagam.entity.LignePanier;
import com.pagam.entity.Panier;
import com.pagam.entity.Produit;
import com.pagam.entity.Utilisateur;
import com.pagam.repository.PanierRepository;
import com.pagam.repository.ProduitRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class PanierService {

    private final HttpSession session;
    private final PanierRepository panierRepository;
    private final ProduitRepository produitRepository;

    // Récupérer le panier de la session ou le créer
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

        Panier panier = getPanierFromSession();

        LignePanier ligne = LignePanier.builder()
                .produit(produit)
                .quantite(quantite)
                .prix(produit.getPrix())
                .build();

        panier.addItem(ligne);           // ajout en mémoire
        panierRepository.save(panier);    // sauvegarde en DB
        session.setAttribute("panier", panier); // mise à jour session
    }

    // Modifier la quantité
    public void modifierQuantite(Long produitId, int quantite) throws Exception {
        Panier panier = getPanierFromSession();
        panier.getItems().stream()
                .filter(l -> l.getProduit().getId().equals(produitId))
                .findFirst()
                .ifPresentOrElse(
                        ligne -> {
                            ligne.setQuantite(quantite);
                            ligne.setMontantTotal(ligne.getPrix() * quantite);
                            panierRepository.save(panier);
                        },
                        () -> {
                            throw new RuntimeException("Produit non trouvé dans le panier");
                        }
                );
        panier.getItems().removeIf(l -> l.getQuantite() <= 0);
        panierRepository.save(panier);
        session.setAttribute("panier", panier);
    }

    // Supprimer un produit
    public void supprimerProduit(Long produitId) {
        Panier panier = getPanierFromSession();
        panier.removeItem(produitId);
        panierRepository.save(panier);
        session.setAttribute("panier", panier);
    }

    // Vider le panier
    public void viderPanier() {
        Panier panier = getPanierFromSession();
        panier.getItems().clear();
        panier.setTotal(0.0);
        panierRepository.save(panier);
        session.setAttribute("panier", panier);
    }

    // Valider le panier et créer les commandes (simplifié)
    public void validerPanier(Utilisateur utilisateur) throws Exception {
        Panier panier = getPanierFromSession();
        if (panier.getItems().isEmpty()) {
            throw new Exception("Panier vide, impossible de valider");
        }
        panier.setUtilisateur(utilisateur);
        panierRepository.save(panier);
        // Ici tu peux créer les commandes et les sauvegarder dans la DB
        viderPanier(); // on vide la session après validation
    }
}
