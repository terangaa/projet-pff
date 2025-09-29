package com.pagam.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@ToString(exclude = {"producteur", "commandes"})
@EqualsAndHashCode(exclude = {"producteur", "commandes"})
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private Double prix;
    private int stock;
    private String image;
    private String description;
    private Integer quantite;  // <--- ce champ doit exister
    @ManyToOne
    @JoinColumn(name = "agriculteur_id")
    private Producteur agriculteur;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producteur_id")
    @JsonIgnoreProperties("produits") // évite la sérialisation infinie JSON
    private Producteur producteur;

    // ✅ Ajout relation avec Commande
    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("produit") // évite la boucle infinie Produit → Commande → Produit
    private List<Commande> commandes = new ArrayList<>();

}