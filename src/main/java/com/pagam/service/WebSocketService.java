package com.pagam.service;

import com.pagam.entity.Capteur;
import com.pagam.entity.Mesure;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void envoyerMesure(Capteur capteur, Mesure mesure, String alerte, String messageML) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", capteur.getId());
        payload.put("reference", capteur.getReference());
        payload.put("type", capteur.getType());
        payload.put("localisation", capteur.getLocalisation());
        payload.put("temperature", mesure.getTemperature());
        payload.put("humidite", mesure.getHumidite());
        payload.put("luminosite", mesure.getLuminosite());
        payload.put("alerte", alerte);
        payload.put("timestamp", mesure.getTimestamp());

        messagingTemplate.convertAndSend("/topic/alertes", payload);
    }
}
