package com.pagam.service;

import com.pagam.entity.Commande;
import com.pagam.entity.Vente;
import com.pagam.repository.VenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VenteService {

    private final VenteRepository venteRepository;
    private final ProduitService produitService;
    private final UtilisateurService utilisateurService;
    private final CommandeService commandeService;

    // 🔹 Récupérer toutes les ventes triées par date décroissante
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
        // Récupérer les entités liées pour éviter TransientObjectException
        if (vente.getProduit() != null && vente.getProduit().getId() != null) {
            vente.setProduit(produitService.findById(vente.getProduit().getId()));
        }
        if (vente.getAcheteur() != null && vente.getAcheteur().getId() != null) {
            vente.setAcheteur(utilisateurService.findById(vente.getAcheteur().getId()));
        }
        if (vente.getCommande() != null && vente.getCommande().getId() != null) {
            vente.setCommande(commandeService.findById(vente.getCommande().getId()));
        }

        // Calcul automatique du montant total
        vente.calculerMontantTotal();

        return venteRepository.save(vente);
    }

    @Transactional
    public void deleteById(Long id) {
        Vente vente = findById(id);
        if (vente != null) {
            // Détacher les relations pour éviter les exceptions Hibernate
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

    // 🔹 Créer une vente à partir d'une commande validée
    @Transactional
    public Vente creerVenteDepuisCommande(Commande commande) {
        if (commande == null || commande.getId() == null) {
            throw new IllegalArgumentException("La commande doit être persistée avant de créer une vente.");
        }

        // Vérifier si une vente existe déjà pour cette commande
        if (venteRepository.existsByCommande(commande)) {
            return (Vente) venteRepository.findByCommande(commande); // Retourner la vente existante
        }

        // Création de la vente
        Vente vente = new Vente();
        vente.setProduit(commande.getProduit());
        vente.setQuantite(commande.getQuantite() != null ? commande.getQuantite() : 0);
        vente.setPrix(commande.getProduit().getPrix() != null ? commande.getProduit().getPrix() : 0.0);
        vente.setCommande(commande);
        vente.setDateVente(LocalDateTime.now());
        vente.setAcheteur(commande.getAcheteur());
        vente.calculerMontantTotal();

        Vente savedVente = venteRepository.save(vente);

        // Lier la vente à la commande (bidirectionnel)
        commande.setVente(savedVente);

        return savedVente;
    }

    public List<Vente> findAll() {
        return venteRepository.findAll();
    }
}
