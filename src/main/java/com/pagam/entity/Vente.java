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

    private LocalDateTime dateVente;

    @ManyToOne
    private Commande commande;

    @ManyToOne
    private Produit produit;

    @ManyToOne
    private Utilisateur acheteur;

    @ManyToOne
    private Utilisateur agriculteur;

    private Integer quantite;
    private Double prix;
    private Double montantTotal;

    @PrePersist
    public void initialiser() {
        if (dateVente == null) dateVente = LocalDateTime.now();
        if (prix == null && produit != null) prix = produit.getPrix();
        montantTotal = (prix != null ? prix : 0.0) * (quantite != null ? quantite : 0);
    }
}
