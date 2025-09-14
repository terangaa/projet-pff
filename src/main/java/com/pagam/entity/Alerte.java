package com.pagam.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alerte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private String type; // HUMIDITE, TEMP, SOL…

    private String capteurReference;

    private String localisation;

    private double valeur;

    private LocalDateTime dateAlerte;

    private LocalDateTime timestamp;

}
