package com.pagam.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Mesure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Capteur capteur;

    private double temperature;
    private double humidite;
    private double luminosite;
    private double valeur; // la mesure réelle

    private int besoinEau;

    private String horodatage;         // Stocke date/heure formatée
    private LocalDateTime dateMesure;  // Stocke date/heure réelle pour tri

    @PrePersist
    public void prePersist() {
        if (dateMesure == null) {
            dateMesure = LocalDateTime.now();
        }
        if (horodatage == null || horodatage.isEmpty()) {
            horodatage = dateMesure.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        }
    }

    public String getTimestamp() {
        return horodatage;
    }

}
