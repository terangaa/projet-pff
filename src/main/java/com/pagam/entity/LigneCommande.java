package com.pagam.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantite;
    private Double prixUnitaire;
    private Double sousTotal;

    @ManyToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;

    @ManyToOne
    @JoinColumn(name = "commande_id")
    private Commande commande;

    @PrePersist
    @PreUpdate
    public void calculerSousTotal() {
        if (produit != null && prixUnitaire == null) {
            prixUnitaire = produit.getPrix();
        }
        this.sousTotal = (prixUnitaire != null ? prixUnitaire : 0.0) * quantite;
    }
}

