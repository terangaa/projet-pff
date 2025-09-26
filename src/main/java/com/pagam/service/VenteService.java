package com.pagam.service;

import com.pagam.entity.Commande;
import com.pagam.entity.Produit;
import com.pagam.entity.Utilisateur;
import com.pagam.entity.Vente;
import com.pagam.repository.VenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VenteService {

    private final VenteRepository venteRepository;
    private final ProduitService produitService;
    private final UtilisateurService utilisateurService;
    private final CommandeService commandeService;

    // Récupérer toutes les ventes triées par date
    public List<Vente> findAllVentes() {
        return venteRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Vente::getDateVente, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    public Vente findById(Long id) {
        return venteRepository.findById(id).orElse(null);
    }

    @Transactional
    public Vente save(Vente vente) {
        // Récupérer les entités liées depuis la DB pour éviter TransientObjectException
        if (vente.getProduit() != null && vente.getProduit().getId() != null) {
            vente.setProduit(produitService.findById(vente.getProduit().getId()));
        }

        if (vente.getAcheteur() != null && vente.getAcheteur().getId() != null) {
            vente.setAcheteur(utilisateurService.findById(vente.getAcheteur().getId()));
        }

        if (vente.getCommande() != null && vente.getCommande().getId() != null) {
            vente.setCommande(commandeService.findById(vente.getCommande().getId()));
        }

        // Calcul du montant total
        vente.calculerMontantTotal();
        return venteRepository.save(vente);
    }

    @Transactional
    public void deleteById(Long id) {
        Vente vente = findById(id);
        if (vente != null) {
            // Détacher les relations pour éviter Hibernate exception
            vente.setAcheteur(null);
            vente.setProduit(null);
            vente.setCommande(null);
            venteRepository.delete(vente);
        }
    }

    public String formatDate(LocalDateTime date) {
        if (date == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return date.format(formatter);
    }

    public void creerVenteDepuisCommande(Commande commande) {
        if (commande == null || commande.getId() == null) {
            throw new IllegalArgumentException("La commande doit être persistée avant de créer une vente.");
        }

        // Vérifier si une vente existe déjà pour cette commande
        if (venteRepository.existsByCommande(commande)) {
            return; // Vente déjà créée
        }

        // Créer la vente
        Vente vente = new Vente();

        // Produit
        if (commande.getProduit() == null || commande.getProduit().getId() == null) {
            throw new IllegalStateException("Le produit de la commande doit être persisté.");
        }
        vente.setProduit(commande.getProduit());

        // Quantité
        vente.setQuantite(commande.getQuantite() != null ? commande.getQuantite() : 0);

        // Prix unitaire
        vente.setPrix(commande.getProduit().getPrix() != null ? commande.getProduit().getPrix() : 0.0);

        // Commande liée
        vente.setCommande(commande);

        // Date de vente
        vente.setDateVente(LocalDateTime.now());

        // Calcul automatique du montantTotal et montant
        vente.calculerMontantTotal();

        // Si la commande a un acheteur, on l’associe
        if (commande.getAcheteur() != null && commande.getAcheteur().getId() != null) {
            vente.setAcheteur(commande.getAcheteur());
        }

        // Sauvegarder la vente
        venteRepository.save(vente);

        // Optionnel : lier la vente à la commande si relation bidirectionnelle
        commande.setVente(vente);
    }

    public List<Vente> findAll() {
        return venteRepository.findAll();
    }
}
