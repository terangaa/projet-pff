package com.pagam.controller;

import com.pagam.entity.Mesure;
import com.pagam.service.MachineLearningService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class MesureController {

    private final MachineLearningService machineLearningService;

    public MesureController(MachineLearningService machineLearningService) {
        this.machineLearningService = machineLearningService;
    }

    @MessageMapping("/mesure")
    @SendTo("/topic/mesures")
    public Mesure envoyerMesure(Mesure mesure) {
        // Ajoute un horodatage
        mesure.setHorodatage(LocalDateTime.now().toString());

        // Appel du ML pour prédire le besoin en eau
        int prediction = machineLearningService.getPrediction(mesure.getTemperature(), mesure.getHumidite());
        mesure.setBesoinEau(prediction); // Assure-toi que Mesure a un champ "besoinEau"

        return mesure;
    }
}
