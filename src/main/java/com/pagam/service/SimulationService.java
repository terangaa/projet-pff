package com.pagam.service;

import com.pagam.entity.Capteur;
import com.pagam.entity.Mesure;
import com.pagam.entity.MeteoData;
import com.pagam.repository.CapteurRepository;
import com.pagam.repository.MesureRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SimulationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final MesureRepository mesureRepository;
    private final CapteurRepository capteurRepository;
    private final MeteoService meteoService;
    private final JavaMailSender mailSender;
    private final Random random = new Random();

    public SimulationService(SimpMessagingTemplate messagingTemplate,
                             MesureRepository mesureRepository,
                             CapteurRepository capteurRepository,
                             MeteoService meteoService,
                             JavaMailSender mailSender) {
        this.messagingTemplate = messagingTemplate;
        this.mesureRepository = mesureRepository;
        this.capteurRepository = capteurRepository;
        this.meteoService = meteoService;
        this.mailSender = mailSender;
    }

    @Scheduled(fixedRate = 10000) // toutes les 60 secondes
    public void simulerDonnees() {
        List<Capteur> capteurs = capteurRepository.findAll();

        for (Capteur capteur : capteurs) {
            try {
                // Envoi de la mesure pour ce capteur
                envoyerMesure(capteur);

                // Pause entre deux capteurs pour éviter l'envoi simultané
                Thread.sleep(2000); // 2 secondes de délai, à ajuster selon le nombre de capteurs
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }

    private void envoyerMesure(Capteur capteur) {
        try {
            double latitude = capteur.getLatitude();
            double longitude = capteur.getLongitude();
            if (latitude == 0 || longitude == 0) return;

            // --- Données météo réelles ---
            Map<String, Object> meteoMap = meteoService.getMeteoByLatLon(latitude, longitude);
            MeteoData meteo = meteoService.parseMeteo(meteoMap);

            double baseTemp = meteo.getTemperature();
            double baseHum = meteo.getHumidite();

            double temperature = baseTemp + (random.nextDouble() * 0.2 - 0.1);
            double humidite = baseHum + (random.nextDouble() * 1 - 0.5);

            // --- Calcul luminosité ---
            Map<String, Object> sys = (Map<String, Object>) meteoMap.get("sys");
            Map<String, Object> clouds = (Map<String, Object>) meteoMap.get("clouds");

            long sunrise = ((Number) sys.get("sunrise")).longValue() * 1000;
            long sunset = ((Number) sys.get("sunset")).longValue() * 1000;
            long now = System.currentTimeMillis();

            int cloudiness = (int) clouds.get("all");
            double luminosite;

            if (now < sunrise || now > sunset) {
                luminosite = 0;
            } else {
                double baseLum = 1000 * (1 - (cloudiness / 100.0));
                luminosite = baseLum + (random.nextDouble() * 2 - 1);
            }
            luminosite = Math.round(luminosite * 10.0) / 10.0;

            // --- Appel modèle ML Flask ---
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
                    niveauAlerte = prediction == 1 ? "🔴" : "🟢";
                    if (messageML.contains("attention") || messageML.contains("risque")) {
                        niveauAlerte = "🟡";
                    }
                }
            } catch (Exception e) {
                messageML = "❌ Erreur Flask: " + e.getMessage();
                niveauAlerte = "🔴";
            }

            // --- Enregistrement BDD ---
            Mesure mesure = Mesure.builder()
                    .capteur(capteur)
                    .temperature(temperature)
                    .humidite(humidite)
                    .luminosite(luminosite)
                    .valeur((temperature + humidite) / 2)
                    .alerte(!niveauAlerte.equals("🟢"))
                    .dateMesure(LocalDateTime.now())
                    .horodatage(LocalDateTime.now().toString())
                    .build();

            mesureRepository.save(mesure);

            // --- Diffusion WebSocket ---
            Map<String, Object> payload = new HashMap<>();
            payload.put("id", capteur.getId()); // ⚠️ essentiel pour le front
            payload.put("localisation", capteur.getLocalisation());
            payload.put("ville", capteur.getVille());
            payload.put("temperature", temperature);
            payload.put("humidite", humidite);
            payload.put("luminosite", luminosite);
            payload.put("alerte", niveauAlerte);
            payload.put("messageML", messageML);

            messagingTemplate.convertAndSend("/topic/alertes/", payload);

            System.out.println("📡 Mesure envoyée pour " + capteur.getVille() + ": " + payload);

            // --- Envoi email si alerte ---
            if (!niveauAlerte.equals("🟢")) {
                String[] emailDest = {"sadikhyade851@gmail.com", "papesy302001@gmail.com"};
                SimpleMailMessage email = new SimpleMailMessage();
                email.setTo(emailDest);
                email.setSubject("⚠️ Alerte météo pour " + capteur.getVille());
                email.setText(
                        "Ville: " + capteur.getVille() + "\n" +
                                "Température: " + temperature + "°C\n" +
                                "Humidité: " + humidite + "%\n" +
                                "Luminosité: " + luminosite + " lux\n" +
                                "Message ML: " + messageML + "\n" +
                                "Alerte: " + niveauAlerte
                );
               // mailSender.send(email);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
