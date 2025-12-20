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
    private String reference;
    private String type;
    private String localisation;
    private String ville;
    private Double latitude;
    private Double longitude;
    private Double moyenne;

    @Transient
    private String iconClass;

    @OneToMany(mappedBy = "capteur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mesure> mesures;

    @ManyToOne
    @JoinColumn(name = "agriculteur_id")
    private Producteur agriculteur;
}
