package com.pagam.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
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
    private LocalDateTime dateCreation;
    private String image;
    private String description;
    private Integer quantite;
    private boolean nouveau;
    private String noteHtml;


    @ManyToOne
    @JoinColumn(name = "agriculteur_id")
    private Producteur agriculteur;

    // ✅ Ajout relation avec Commande
    @OneToMany(mappedBy = "produit", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("produit") // évite la boucle infinie Produit → Commande → Produit
    private List<Commande> commandes = new ArrayList<>();

}