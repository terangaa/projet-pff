package com.pagam.service;

import com.pagam.entity.Alerte;
import com.pagam.repository.AlerteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlerteService {

    private final AlerteRepository alerteRepository;


    public void enregistrer(String message, String type, String reference, String localisation, double valeur) {
        Alerte a = Alerte.builder()
                .message(message)
                .type(type)
                .capteurReference(reference)
                .localisation(localisation)
                .valeur(valeur)
                .timestamp(LocalDateTime.now())
                .build();
        alerteRepository.save(a);
    }

    public List<Alerte> getToutes() {
        return alerteRepository.findAll();
    }

    public List<Alerte> getParType(String type) {
        return alerteRepository.findByTypeOrderByTimestampDesc(type);
    }

    public List<Alerte> toutesLesAlertes() {
        return alerteRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
    }


    public List<Alerte> getAllAlertes() {
        return alerteRepository.findAll();
    }
}
