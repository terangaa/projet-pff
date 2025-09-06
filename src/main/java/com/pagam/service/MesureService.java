package com.pagam.service;

import com.pagam.entity.Capteur;
import com.pagam.entity.Mesure;
import com.pagam.repository.MesureRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MesureService {

    private final MesureRepository mesureRepository;

    public MesureService(MesureRepository mesureRepository) {
        this.mesureRepository = mesureRepository;
    }

    // Récupérer les 5 dernières mesures d’un capteur
    public List<Mesure> getDernieresMesures(Long capteurId) {
        return mesureRepository.findTop5ByCapteur_IdOrderByDateMesureDesc(capteurId);
    }

    // Calculer la moyenne des valeurs d’un capteur
    public Double calculerMoyenne(Capteur capteur) {
        List<Mesure> mesures = mesureRepository.findByCapteur(capteur);
        if (mesures.isEmpty()) {
            return 0.0;
        }
        double somme = mesures.stream()
                .mapToDouble(Mesure::getValeur)
                .sum();
        return somme / mesures.size();
    }

    // Ajouter une nouvelle mesure
    public Mesure ajouterMesure(Mesure mesure) {
        return mesureRepository.save(mesure);
    }

    // Récupérer toutes les mesures d’un capteur, triées par dateMesure décroissante
    public List<Mesure> getMesuresParCapteur(Long capteurId) {
        return mesureRepository.findByCapteurIdOrderByDateMesureDesc(capteurId);
    }
}
