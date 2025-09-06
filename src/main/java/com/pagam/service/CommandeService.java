package com.pagam.service;

import com.pagam.entity.Commande;
import com.pagam.repository.CommandeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandeService {

    private final CommandeRepository commandeRepository; // seul repository à injecter

    // Méthodes
    public Commande saveCommande(Commande commande){
        return commandeRepository.save(commande);
    }

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


    public Commande getCommandeById(Long id){
        return commandeRepository.findById(id).orElse(null);
    }

    public void deleteCommande(Long id){
        commandeRepository.deleteById(id);
    }
}
