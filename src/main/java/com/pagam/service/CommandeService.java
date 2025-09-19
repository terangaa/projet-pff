package com.pagam.service;

import com.pagam.entity.Commande;
import com.pagam.entity.Utilisateur;
import com.pagam.repository.CommandeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandeService {

    private final CommandeRepository commandeRepository;

    // Sauvegarder une commande
    public Commande saveCommande(Commande commande){
        return commandeRepository.save(commande);
    }

    // Toutes les commandes (admin)
    public List<Commande> getAllCommandes() {
        List<Commande> commandes = commandeRepository.findAll();
        for (Commande c : commandes) {
            if (c.getProduit() != null && c.getQuantite() != null) {
                c.setPrixTotal(c.getProduit().getPrix() * c.getQuantite());
            } else {
                c.setPrixTotal(0.0);
            }
        }
        return commandes;
    }

    // Une commande par ID
    public Commande getCommandeById(Long id){
        return commandeRepository.findById(id).orElse(null);
    }

    // Supprimer une commande
    public void deleteCommande(Long id){
        commandeRepository.deleteById(id);
    }

    // ✅ Récupérer les commandes d’un acheteur spécifique
    public List<Commande> getCommandesByAcheteur(Utilisateur acheteur){
        List<Commande> commandes = commandeRepository.findByAcheteur(acheteur);
        for (Commande c : commandes) {
            if (c.getProduit() != null && c.getQuantite() != null) {
                c.setPrixTotal(c.getProduit().getPrix() * c.getQuantite());
            } else {
                c.setPrixTotal(0.0);
            }
        }
        return commandes;
    }
}
