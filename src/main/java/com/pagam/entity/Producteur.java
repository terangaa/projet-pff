package com.pagam.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"produits", "utilisateur"}) // ✅ empêche la récursion infinie
@EqualsAndHashCode(exclude = {"produits", "utilisateur"})
public class Producteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String prenom;
    private String nom;
    private String email;

    // ✅ Relation vers l'utilisateur associé
    @OneToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    // ✅ Produits liés au producteur
    @OneToMany(mappedBy = "agriculteur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Produit> produits = new ArrayList<>();

    // === Méthodes helper ===
    public void addProduit(Produit produit) {
        produits.add(produit);
        produit.setAgriculteur(this);
    }

    public void removeProduit(Produit produit) {
        produits.remove(produit);
        produit.setAgriculteur(null);
    }
}
