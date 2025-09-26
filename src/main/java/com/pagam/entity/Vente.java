package com.pagam.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;

    @ManyToOne
    @JoinColumn(name = "acheteur_id")
    private Utilisateur acheteur;

    private Integer quantite;
    private Double prix;          // prix unitaire
    private Double montantTotal;  // prix * quantité
    private LocalDateTime dateVente;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    @ToString.Exclude
    private Commande commande;

    private Double montant;       // montant final (peut être lié à commande)

    // 🔹 Setter sécurisé pour Produit
    public void setProduit(Produit produit) {
        if (produit != null && produit.getId() == null) {
            throw new IllegalStateException("Le produit doit être persisté avant d'être assigné à la vente.");
        }
        this.produit = produit;
        calculerMontantTotal();
    }

    // 🔹 Setter sécurisé pour Acheteur
    public void setAcheteur(Utilisateur acheteur) {
        if (acheteur != null && acheteur.getId() == null) {
            throw new IllegalStateException("L'utilisateur doit être persisté avant d'être assigné à la vente.");
        }
        this.acheteur = acheteur;
    }

    // 🔹 Setter sécurisé pour Commande
    public void setCommande(Commande commande) {
        if (commande != null && commande.getId() == null) {
            throw new IllegalStateException("La commande doit être persistée avant d'être assignée à la vente.");
        }
        this.commande = commande;
        calculerMontant();
    }

    // 🔹 Calcul du montant final à partir de la commande ou du montantTotal
    public void calculerMontant() {
        if (commande != null && commande.getPrixTotal() != null) {
            this.montant = commande.getPrixTotal();
        } else if (montantTotal != null) {
            this.montant = montantTotal;
        } else {
            this.montant = 0.0;
        }
    }

    // 🔹 Calcul automatique du montantTotal à partir du prix et de la quantité
    public void calculerMontantTotal() {
        if (prix != null && quantite != null) {
            this.montantTotal = prix * quantite;
        } else {
            this.montantTotal = 0.0;
        }
        calculerMontant();
    }

    // 🔹 Setter prix sécurisé
    public void setPrix(Double prix) {
        this.prix = prix != null ? prix : 0.0;
        calculerMontantTotal();
    }

    // 🔹 Setter quantité sécurisé
    public void setQuantite(Integer quantite) {
        this.quantite = quantite != null ? quantite : 0;
        calculerMontantTotal();
    }
}
