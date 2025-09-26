package com.pagam.service;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;

public class MeteoSimuler {

    private final Random random = new Random();

    public Map<String, Object> simulerMesure(String localisation) {
        Map<String, Object> mesure = new HashMap<>();

        // Température réaliste selon type de ville
        double baseTemp;
        double baseHum;
        double baseLum;

        // Ajuster selon la localisation (optionnel)
        switch (localisation.toLowerCase()) {
            case "matam":
                baseTemp = 34 + random.nextDouble() * 3;  // 34→37°C
                baseHum = 30 + random.nextDouble() * 15;  // 30→45%
                break;
            case "louga":
                baseTemp = 12 + random.nextDouble() * 3;  // 12→15°C
                baseHum = 45 + random.nextDouble() * 20;  // 45→65%
                break;
            case "touba":
                baseTemp = 27 + random.nextDouble() * 2;  // 27→29°C
                baseHum = 70 + random.nextDouble() * 10;  // 70→80%
                break;
            default: // Fatick, Mbour, Parcelle, Bargny, Diourbel, etc.
                baseTemp = 30 + random.nextDouble() * 5;  // 30→35°C
                baseHum = 50 + random.nextDouble() * 20; // 50→70%
        }

        baseLum = 100 + random.nextDouble() * 500; // 100→600 lux

        // Ajouter au résultat
        mesure.put("localisation", localisation);
        mesure.put("temperature", Math.round(baseTemp * 10.0) / 10.0);
        mesure.put("humidite", Math.round(baseHum * 10.0) / 10.0);
        mesure.put("luminosite", Math.round(baseLum * 10.0) / 10.0);

        // Générer un message ML et alerte
        String messageML;
        String alerte;

        if (baseTemp > 35) {
            messageML = "🌞 Température très élevée (" + mesure.get("temperature") + "°C), arroser vite !";
            alerte = "🔴";
        } else if (baseTemp > 32) {
            messageML = "🔥 Température élevée (" + mesure.get("temperature") + "°C), risque de stress thermique.";
            alerte = "🟡";
        } else if (baseTemp < 15) {
            messageML = "🥶 Température basse (" + mesure.get("temperature") + "°C), attention aux plantes.";
            alerte = "🟡";
        } else {
            messageML = "✅ Conditions favorables (" + mesure.get("temperature") + "°C).";
            alerte = "🟢";
        }

        mesure.put("messageML", messageML);
        mesure.put("alerte", alerte);

        return mesure;
    }
}
