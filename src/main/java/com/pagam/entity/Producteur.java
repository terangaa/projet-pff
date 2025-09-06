package com.pagam.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"produits"})
@EqualsAndHashCode(exclude = {"produits"})
public class Producteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    @OneToMany(mappedBy = "producteur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Produit> produits;
}
