package com.pagam.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Panier implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double total = 0.0;

    @ManyToOne
    private Utilisateur utilisateur;

    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LignePanier> lignes = new ArrayList<>();

    // Méthode pour ajouter un item
    public void addItem(LignePanier item) {
        lignes.stream()
                .filter(i -> i.getProduit().getId().equals(item.getProduit().getId()))
                .findFirst()
                .ifPresentOrElse(
                        existingItem -> {
                            existingItem.setQuantite(existingItem.getQuantite() + item.getQuantite());
                            existingItem.setMontantTotal(existingItem.getPrix() * existingItem.getQuantite());
                        },
                        () -> {
                            item.setMontantTotal(item.getPrix() * item.getQuantite());
                            item.setPanier(this); // lier le panier à la ligne
                            lignes.add(item);
                        }
                );
        calculerTotal();
    }

    // Méthode pour supprimer un item
    public void removeItem(Long produitId) {
        lignes.removeIf(item -> item.getProduit().getId().equals(produitId));
        calculerTotal();
    }

    // Calculer le total du panier
    private void calculerTotal() {
        this.total = lignes.stream()
                .mapToDouble(LignePanier::getMontantTotal)
                .sum();
    }

    public List<LignePanier> getItems() {
        return this.lignes;
    }

}
