package com.pagam.service;

import java.util.Random;

public class MesureSimuler{

    private static final Random random = new Random();

    public static void main(String[] args) {
        double temperatureMeteo = 27.36; // valeur récupérée depuis OpenWeatherMap
        double humiditeMeteo = 83.0;

        for (int i = 0; i < 5; i++) {
            double temperature = temperatureMeteo + random.nextGaussian() * 2; // fluctuation ±2°C
            double humidite = humiditeMeteo + random.nextGaussian() * 5;       // fluctuation ±5%
            double luminosite = 300 + random.nextDouble() * 700;                // 300 à 1000 lux

            String messageML = generateMessage(temperature, humidite);

            System.out.println("📡 Mesure simulée : {"
                    + "temperature=" + String.format("%.2f", temperature)
                    + ", humidite=" + String.format("%.2f", humidite)
                    + ", luminosite=" + String.format("%.2f", luminosite)
                    + ", messageML=" + messageML
                    + "}");
        }
    }

    private static String generateMessage(double temperature, double humidite) {
        if (humidite < 60 || temperature > 30) {
            return "⚠️ Attention : Irrigation nécessaire ou chaleur élevée.";
        } else {
            return "👌 Humidité et température optimales pour les cultures.";
        }
    }
}

