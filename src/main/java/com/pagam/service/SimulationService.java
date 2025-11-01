package com.pagam.service;

import com.pagam.entity.Capteur;
import com.pagam.entity.Mesure;
import com.pagam.entity.MeteoData;
import com.pagam.repository.CapteurRepository;
import com.pagam.repository.MesureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

@Service
public class SimulationService {

    private static final Logger logger = LoggerFactory.getLogger(SimulationService.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final MesureRepository mesureRepository;
    private final CapteurRepository capteurRepository;
    private final MeteoService meteoService;
    private final JavaMailSender mailSender;
    private final Random random = new Random();

    private final ExecutorService executor = Executors.newFixedThreadPool(5);

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

    @Scheduled(fixedRate = 10000)
    public void simulerDonnees() {
        List<Capteur> tousCapteurs = capteurRepository.findAll();
        Map<String, List<Capteur>> capteursParVille = new HashMap<>();

        // Grouper les capteurs par ville
        for (Capteur c : tousCapteurs) {
            capteursParVille.computeIfAbsent(c.getVille(), k -> new ArrayList<>()).add(c);
        }

        // Sélectionner un capteur aléatoire par ville
        List<Capteur> capteursAleatoires = new ArrayList<>();
        for (Map.Entry<String, List<Capteur>> entry : capteursParVille.entrySet()) {
            List<Capteur> capteursVille = entry.getValue();
            Capteur capteurChoisi = capteursVille.get(random.nextInt(capteursVille.size()));
            capteursAleatoires.add(capteurChoisi);
        }

        // Lancer la simulation pour ces capteurs
        for (Capteur capteur : capteursAleatoires) {
            executor.submit(() -> {
                try {
                    envoyerMesure(capteur);
                } catch (Exception e) {
                    logger.error("Erreur simulation capteur {}: {}", capteur.getId(), e.getMessage(), e);
                }
            });
        }
    }

    private void envoyerMesure(Capteur capteur) {
        double latitude = capteur.getLatitude();
        double longitude = capteur.getLongitude();
        if (latitude == 0 || longitude == 0) return;

        try {
            // --- Données météo réelles ---
            Map<String, Object> meteoMap = meteoService.getMeteoByLatLon(latitude, longitude);
            MeteoData meteo = meteoService.parseMeteo(meteoMap);

            double temperature = meteo.getTemperature() + (random.nextDouble() * 0.2 - 0.1);
            double humidite = meteo.getHumidite() + (random.nextDouble() * 1 - 0.5);

            // --- Calcul luminosité ---
            Map<String, Object> sys = (Map<String, Object>) meteoMap.get("sys");
            Map<String, Object> clouds = (Map<String, Object>) meteoMap.get("clouds");
            long sunrise = ((Number) sys.get("sunrise")).longValue() * 1000;
            long sunset = ((Number) sys.get("sunset")).longValue() * 1000;
            long now = System.currentTimeMillis();
            int cloudiness = (int) clouds.get("all");
            double luminosite = (now < sunrise || now > sunset) ? 0
                    : Math.round((1000 * (1 - cloudiness / 100.0) + (random.nextDouble() * 2 - 1)) * 10.0) / 10.0;

            // --- Simulation insectes ---
            int insectes = random.nextInt(20); // 0 à 19 insectes
            boolean alerteInsectes = insectes > 10; // seuil d'alerte

            // --- Appel modèle ML Flask ---
            String url = "http://localhost:5000/predict";
            Map<String, Object> jsonData = Map.of("temperature", temperature, "humidite", humidite);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(jsonData, headers);

            String messageML = "";
            String niveauAlerte = "🟢";

            try {
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
                if (response.getBody() != null) {
                    messageML = (String) response.getBody().get("message");
                    int prediction = (Integer) response.getBody().get("prediction");
                    niveauAlerte = prediction == 1 ? "🔴" : "🟢";
                    if (messageML.contains("attention") || messageML.contains("risque")) niveauAlerte = "🟡";
                }
            } catch (Exception e) {
                messageML = "❌ Erreur Flask: " + e.getMessage();
                niveauAlerte = "🔴";
                logger.warn("Erreur Flask capteur {}: {}", capteur.getId(), e.getMessage());
            }

            // --- Intégrer alerte insectes ---
            if (alerteInsectes) {
                niveauAlerte = "🟠"; // orange pour infestation
                messageML += " ⚠️ Risque insectes élevé";
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
                    .insectes(insectes)
                    .build();
            mesureRepository.save(mesure);

            // --- Diffusion WebSocket ---
            Map<String, Object> payload = new HashMap<>();
            payload.put("id", capteur.getId());
            payload.put("localisation", capteur.getLocalisation());
            payload.put("ville", capteur.getVille());
            payload.put("temperature", temperature);
            payload.put("humidite", humidite);
            payload.put("luminosite", luminosite);
            payload.put("insectes", insectes);
            payload.put("alerteInsectes", alerteInsectes);
            payload.put("alerte", niveauAlerte);
            payload.put("messageML", messageML);
            messagingTemplate.convertAndSend("/topic/alertes/", payload);

            logger.info("Mesure envoyée {} : {}", capteur.getVille(), payload);

            // --- Envoi email si alerte ---
            if (!niveauAlerte.equals("🟢")) {
                String[] emailDest = {"sadikhyade851@gmail.com", "papesy302001@gmail.com"};
                SimpleMailMessage email = new SimpleMailMessage();
                email.setTo(emailDest);
                email.setSubject("⚠️ Alerte pour " + capteur.getVille());
                email.setText(
                        "Ville: " + capteur.getVille() + "\n" +
                                "Température: " + temperature + "°C\n" +
                                "Humidité: " + humidite + "%\n" +
                                "Luminosité: " + luminosite + " lux\n" +
                                "Insectes détectés: " + insectes + "\n" +
                                "Message ML: " + messageML + "\n" +
                                "Alerte: " + niveauAlerte
                );
                // mailSender.send(email);
            }

        } catch (Exception e) {
            logger.error("Erreur capteur {}: {}", capteur.getId(), e.getMessage(), e);
        }
    }
}
