package com.pagam.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Autoriser le front, ressources statiques, H2 console et endpoints publics
                        .requestMatchers(
                                "/", "/index.html",
                                "/templates/fragments/**",
                                "/alertes",
                                "/utilisateurs/**","/producteurs/**","/produits/**","/commandes/**",
                                "/favicon.ico","/capteurs/**","/simulations/**",
                                "/css/**", "/js/**", "/images/**",
                                "/ws/**",
                                "/api/capteurs/**", "/api/utilisateurs/**",
                                "/api/auth/**", "/auth/**",
                                "/h2-console/**",
                                "/api/alertes/**", "/api/produits/**",
                                "/api/producteurs/**", "/api/commandes/**"
                        ).permitAll()
                        // Tout le reste nécessite authentification
                        .anyRequest().authenticated()
                )
                // Session stateless pour JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Ajouter le filtre JWT avant UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                // Autoriser H2 console en mode iframe
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
