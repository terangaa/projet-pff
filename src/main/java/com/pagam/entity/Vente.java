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
    private Utilisateur acheteur; // si tu veux gérer l’utilisateur connecté

    private int quantite;
    private Double prix; // prix unitaire
    private double montantTotal;

    private LocalDateTime dateVente;
}
