package com.pagam.service;

import com.pagam.entity.MeteoData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class MeteoService {

    // Injection de la clé API depuis application.properties
    @Value("${openweathermap.api.key}")
    private String apiKey;

    private static final String URL_TEMPLATE =
            "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric";

    // --- 1️⃣ Récupération météo par ville ---
    public MeteoData getMeteo(String ville) {
        String url = String.format(URL_TEMPLATE, ville, apiKey);
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || !response.containsKey("main")) {
            throw new RuntimeException("Réponse météo invalide pour " + ville);
        }

        Map<String, Object> main = (Map<String, Object>) response.get("main");
        double temperature = ((Number) main.get("temp")).doubleValue();
        double humidite = ((Number) main.get("humidity")).doubleValue();

        return new MeteoData(temperature, humidite);
    }

    // --- 2️⃣ Récupération météo brute par ville ---
    public Map<String, Object> getMeteoRaw(String localisation) {
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?q=%s,SN&appid=%s&units=metric",
                localisation, apiKey
        );

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || !response.containsKey("main")) {
            throw new RuntimeException("Réponse météo invalide pour la localisation : " + localisation);
        }

        return response;
    }

    // --- 3️⃣ Récupération météo par latitude/longitude ---
    public Map<String, Object> getMeteoByLatLon(double latitude, double longitude) {
        String url = String.format(
                "https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=%s&units=metric",
                latitude, longitude, apiKey
        );

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || !response.containsKey("main")) {
            throw new RuntimeException("Réponse météo invalide pour les coordonnées : " + latitude + ", " + longitude);
        }

        return response;
    }

    // --- 4️⃣ Parsing météo brute en MeteoData ---
    public MeteoData parseMeteo(Map<String, Object> meteoMap) {
        if (meteoMap == null || !meteoMap.containsKey("main")) {
            throw new RuntimeException("Réponse météo invalide");
        }

        Map<String, Object> main = (Map<String, Object>) meteoMap.get("main");
        double temperature = ((Number) main.get("temp")).doubleValue();
        double humidite = ((Number) main.get("humidity")).doubleValue();

        return new MeteoData(temperature, humidite);
    }
}
