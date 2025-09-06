package com.pagam.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Capteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String reference; // ex: CAP-001
    private String type; // SOL, HUMIDITE, TEMP
    private String localisation; // région ou GPS

    private Double moyenne; // ← doit être présent

    @Transient // pas besoin de le stocker en base
    private String iconClass;

    @OneToMany(mappedBy = "capteur", cascade = CascadeType.ALL)
    private List<Mesure> mesures;


}
