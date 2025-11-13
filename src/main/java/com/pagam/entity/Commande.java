package com.pagam.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Utilisateur acheteur;

    @ManyToOne
    private Produit produit;

    private Integer quantite;

    private Double prixUnitaire;

    private Double prixTotal;

    private LocalDateTime dateCommande;

    private String adresseLivraison;


    @OneToOne(mappedBy = "commande", cascade = CascadeType.ALL)
    private Vente vente;

    @Enumerated(EnumType.STRING)
    private StatutCommande statut = StatutCommande.EN_COURS;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // ← IMPORTANT: Initialiser avec Builder
    private List<LigneCommande> lignes = new ArrayList<>();

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    @Builder.Default // ← IMPORTANT: Initialiser avec Builder
    private List<Vente> ventes = new ArrayList<>();



    @PrePersist
    public void initialiser() {
        // Initialiser les listes si elles sont null
        if (lignes == null) {
            lignes = new ArrayList<>();
        }
        if (ventes == null) {
            ventes = new ArrayList<>();
        }

        if (dateCommande == null) {
            dateCommande = LocalDateTime.now();
        }

        // Initialiser prixTotal si null
        if (prixTotal == null) {
            prixTotal = 0.0;
        }

        // Initialiser prixUnitaire si null
        if (prixUnitaire == null && produit != null && produit.getPrix() != null) {
            prixUnitaire = produit.getPrix();
        }

        recalculerTotal();
    }

    public void addLigne(LigneCommande ligne) {
        if (lignes == null) {
            lignes = new ArrayList<>();
        }
        ligne.setCommande(this);
        lignes.add(ligne);
        recalculerTotal();
    }

    public void recalculerTotal() {
        // Vérifier que lignes n'est pas null avant d'utiliser stream()
        if (lignes != null && !lignes.isEmpty()) {
            this.prixTotal = lignes.stream()
                    .mapToDouble(LigneCommande::getSousTotal)
                    .sum();
        } else if (produit != null && quantite != null && prixUnitaire != null) {
            // Calculer à partir des champs de base si pas de lignes
            this.prixTotal = prixUnitaire * quantite;
        }
    }
}