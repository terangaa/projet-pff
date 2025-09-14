package com.pagam.security;

import com.pagam.entity.Utilisateur;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    // Extraire l'email (subject)
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Extraire le rôle
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // Générer un token avec email + rôle
    public String generateToken(Utilisateur user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name()) // ✅ stocke le rôle
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    // Vérifier si un token est valide
    public boolean isTokenValid(String token, String username) {
        final String email = extractUsername(token);
        return (email.equals(username) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public long getExpiration() {
        return this.expiration;
    }

}
