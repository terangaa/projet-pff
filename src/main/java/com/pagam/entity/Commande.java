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

    @OneToOne(mappedBy = "commande", cascade = CascadeType.ALL)
    private Vente vente;


    @Enumerated(EnumType.STRING)
    private StatutCommande statut = StatutCommande.EN_COURS;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneCommande> lignes = new ArrayList<>();

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    private List<Vente> ventes = new ArrayList<>();

    @PrePersist
    public void initialiser() {
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
        ligne.setCommande(this);
        lignes.add(ligne);
        recalculerTotal();
    }

    public void recalculerTotal() {
        this.prixTotal = lignes.stream()
                .mapToDouble(LigneCommande::getSousTotal)
                .sum();
    }
}
