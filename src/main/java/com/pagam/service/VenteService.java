package com.pagam.service;

import com.pagam.entity.Producteur;
import com.pagam.entity.Produit;
import com.pagam.entity.Vente;
import com.pagam.repository.VenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VenteService {

    private final VenteRepository venteRepository;

    // Récupérer les ventes par producteur
    public List<Vente> ventesParAgriculteur(Producteur producteur) {
        return venteRepository.findByProduit_Producteur(producteur);
    }

    // Récupérer les ventes par produit
    public List<Vente> ventesParProduit(Produit produit) {
        return venteRepository.findByProduit(produit);
    }

    // Ajouter ou sauvegarder une vente
    public Vente saveVente(Vente vente){
        return venteRepository.save(vente);
    }

    public Vente ajouterVente(Vente vente) {
        // Vérifie que le produit associé à la vente existe
        if (vente.getProduit() == null) {
            throw new IllegalArgumentException("Le produit de la vente ne peut pas être null.");
        }
        // Tu peux ajouter d'autres validations ici, par ex. quantité > 0, prix > 0, etc.

        // Enregistre la vente en base
        return venteRepository.save(vente);
    }

    public void deleteById(Long id) {
         venteRepository.deleteById(id);
    }

    public List<Vente> findAll() {
        return venteRepository.findAll();
    }

    public void save(Vente vente) {
        venteRepository.save(vente);
    }

    public String formatDate(LocalDateTime date) {
        if (date == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return date.format(formatter);
    }

    public List<Vente> findAllVentes() {
        return venteRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Vente::getDateVente, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

}
