package com.pagam.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"producteur", "agriculteur"})
@EqualsAndHashCode(exclude = {"producteur", "agriculteur"})
public class Utilisateur implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String prenom;
    private String nom;
    private String email;
    private String localite;

    @Column(nullable = false, length = 60)
    private String motDePasse;

    @Transient
    private String confirmMotDePasse;

    private String photo;
    private String resetToken;

    // Si un utilisateur est rattaché à un autre agriculteur
    @ManyToOne
    @JoinColumn(name = "agriculteur_id")
    @JsonIgnore
    private Utilisateur agriculteur;

    @Enumerated(EnumType.STRING)
    private Role role;

    // Relation vers Producteur (si l'utilisateur est un producteur)
    @OneToOne(mappedBy = "utilisateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Producteur producteur;

    // ---------------- UserDetails ----------------
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(() -> role.name());
    }

    @Override
    public String getPassword() {
        return motDePasse;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    // Méthode utilitaire pour récupérer les produits
    public List<Produit> getProduits() {
        if (this.producteur != null) {
            return this.producteur.getProduits();
        }
        return Collections.emptyList();
    }
}

