package com.pagam.repository;

import com.pagam.entity.Capteur;
import com.pagam.entity.Mesure;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MesureRepository extends JpaRepository<Mesure, Long> {
    List<Mesure> findTop10ByCapteurIdOrderByHorodatageDesc(Long capteurId);

    List<Mesure> findByCapteur(Capteur capteur);

    List<Mesure> findByCapteurIdOrderByDateMesureDesc(Long capteurId);

    List<Mesure> findTop5ByCapteur_IdOrderByDateMesureDesc(Long capteurId);
}
