package com.pagam.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class SimulationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final Random random = new Random();

    public SimulationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = 10000)
    public void simulerDonnees() {
        double temperature = 15 + random.nextDouble() * 20;
        double humidite = 30 + random.nextDouble() * 50;
        double luminosite = random.nextDouble() * 1000;

        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:5000/predict";

        Map<String, Object> jsonData = new HashMap<>();
        jsonData.put("temperature", temperature);
        jsonData.put("humidite", humidite);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(jsonData, headers);

        String messageML = "";
        String niveauAlerte = "🟢";

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            if (response.getBody() != null) {
                messageML = (String) response.getBody().get("message");
                int prediction = (Integer) response.getBody().get("prediction");
                double confidence = (Double) response.getBody().get("confidence");

                // Déterminer niveau d’alerte selon prediction et confiance
                niveauAlerte = prediction == 1 ? "🔴" : "🟢";
                if (messageML.contains("attention") || messageML.contains("risque")) {
                    niveauAlerte = "🟡";
                }
            }
        } catch (Exception e) {
            messageML = "❌ Erreur Flask: " + e.getMessage();
            niveauAlerte = "🔴";
        }

        String message = String.format("[%s] %s 🌡 %.1f°C | 💧 %.1f%% | ☀️ %.1f lux → %s",
                java.time.LocalDateTime.now(), niveauAlerte, temperature, humidite, luminosite, messageML);

        messagingTemplate.convertAndSend("/topic/alertes", message);
        System.out.println("📡 Mesure envoyée : " + message);
    }
}
