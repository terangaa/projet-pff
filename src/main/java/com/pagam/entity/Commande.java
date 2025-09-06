package com.pagam.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Utilisateur acheteur;

    @ManyToOne(fetch = FetchType.EAGER)
    private Produit produit;

    private Integer quantite;

    private double prixTotal;

    private double prixUnitaire; // ✅ conserver le prix du produit au moment de la commande

    private LocalDateTime dateCommande;

    @Enumerated(EnumType.STRING)
    private StatutCommande statut;

    // ✅ Avant insertion en base
    @PrePersist
    protected void onCreate() {
        if (dateCommande == null) {
            dateCommande = LocalDateTime.now();
        }
        if (produit != null) {
            prixUnitaire = produit.getPrix(); // sauvegarde le prix du produit
        }
        calculerPrixTotal();
    }

    // ✅ Avant mise à jour en base
    @PreUpdate
    protected void onUpdate() {
        if (dateCommande == null) {
            dateCommande = LocalDateTime.now();
        }
        calculerPrixTotal();
    }

    // ✅ Recalcul du prix total
    public void calculerPrixTotal() {
        this.prixTotal = prixUnitaire * quantite;
    }
}
