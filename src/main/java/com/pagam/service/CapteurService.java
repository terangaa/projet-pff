package com.pagam.service;

import com.pagam.entity.Capteur;
import com.pagam.entity.Mesure;
import com.pagam.repository.CapteurRepository;
import com.pagam.repository.MesureRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CapteurService {

    private final CapteurRepository capteurRepository;
    private final MesureRepository mesureRepository;

    public CapteurService(CapteurRepository capteurRepository, MesureRepository mesureRepository) {
        this.capteurRepository = capteurRepository;
        this.mesureRepository = mesureRepository;
    }

    // Tous les capteurs
    public List<Capteur> tousLesCapteurs() {
        return capteurRepository.findAll();
    }

    // Ajouter / Modifier un capteur
    public void save(Capteur capteur) {
        capteurRepository.save(capteur);
    }

    // Récupérer un capteur par ID
    public Capteur getById(Long id) {
        return capteurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Capteur introuvable"));
    }

    // Supprimer un capteur
    public void deleteById(Long id) {
        capteurRepository.deleteById(id);
    }

    // Récupérer les 5 dernières mesures d’un capteur
    public List<Mesure> getDernieresMesures(Long capteurId) {
        return mesureRepository.findTop5ByCapteur_IdOrderByDateMesureDesc(capteurId);
    }

    // Vérifier si une valeur déclenche une alerte
    public boolean estAlerte(double valeur, String type) {
        switch (type.toUpperCase()) {
            case "TEMP":
            case "TEMPERATURE":
                return valeur < 10 || valeur > 35;
            case "HUMIDITE":
            case "HUMIDITY":
                return valeur < 30 || valeur > 70;
            case "SOL":
            case "SOIL":
                return valeur < 20 || valeur > 80;
            default:
                return false;
        }
    }


    // ✅ Méthode pour calculer la moyenne
    public Double calculerMoyenne(Capteur capteur) {
        List<Mesure> mesures = mesureRepository.findByCapteur(capteur);
        if (mesures.isEmpty()) return null;

        double somme = 0;
        for (Mesure m : mesures) {
            somme += m.getValeur();
        }
        return somme / mesures.size();
    }
}
