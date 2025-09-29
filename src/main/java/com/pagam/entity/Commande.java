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

    @ManyToOne(fetch = FetchType.EAGER)
    private Utilisateur acheteur;

    @ManyToOne(fetch = FetchType.EAGER)
    private Produit produit;

    private Integer quantite;

    private Double prixTotal;

    private double prixUnitaire; // prix du produit au moment de la commande

    private LocalDateTime dateCommande;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vente> ventes;

    @Enumerated(EnumType.STRING)
    private StatutCommande statut;

    @PrePersist
    protected void onCreate() {
        if (dateCommande == null) {
            dateCommande = LocalDateTime.now();
        }
        if (produit != null) {
            prixUnitaire = produit.getPrix();
        }
        calculerPrixTotal();
    }

    @PreUpdate
    protected void onUpdate() {
        calculerPrixTotal();
    }

    public void calculerPrixTotal() {
        if (quantite != null) {
            this.prixTotal = prixUnitaire * quantite;
        } else {
            this.prixTotal = 0.0;
        }
    }

    /**
     * Crée une vente à partir de cette commande si elle est validée.
     * La vente est liée à cette commande et au produit/acheteur correspondants.
     */
    public Vente creerVenteDepuisCommande() {
        if (this.statut != StatutCommande.VALIDEE) {
            throw new IllegalStateException("La commande doit être validée avant de créer une vente.");
        }

        Vente vente = Vente.builder()
                .acheteur(this.acheteur)
                .produit(this.produit)
                .quantite(this.quantite != null ? this.quantite : 0)
                .prix(this.prixUnitaire)
                .dateVente(LocalDateTime.now())
                .commande(this)
                .montantTotal(this.prixTotal != null ? this.prixTotal : 0.0)
                .montant(this.prixTotal != null ? this.prixTotal : 0.0)
                .agriculteur(produit.getAgriculteur() != null ? produit.getAgriculteur().getUtilisateur() : null)
                .build();

        // Ajouter la vente à la liste des ventes de la commande (relation bidirectionnelle)
        this.ventes.add(vente);

        return vente;
    }

    public void setVente(Vente savedVente) {
        if (savedVente != null) {
            // Assure que la liste des ventes est initialisée
            if (this.ventes == null) {
                this.ventes = new ArrayList<>();
            }

            // Ajoute la vente à la liste si elle n'y est pas déjà
            if (!this.ventes.contains(savedVente)) {
                this.ventes.add(savedVente);
            }

            // Assure que la vente pointe vers cette commande
            savedVente.setCommande(this);
        }
    }

    public boolean getVente() {
        return ventes != null && !ventes.isEmpty();
    }

}
