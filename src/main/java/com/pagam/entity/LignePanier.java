package com.pagam.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LignePanier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // identifiant unique de la ligne

    @ManyToOne
    @JoinColumn(name = "produit_id") // nom de colonne dans la table
    private Produit produit;

    private int quantite;
    private Double prix;
    private Double montantTotal;

    @ManyToOne
    @JoinColumn(name = "panier_id")
    private Panier panier;
}
