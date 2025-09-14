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
                // 1️⃣ Désactiver CSRF (utile pour les API REST)
                .csrf(csrf -> csrf.disable())

                // 2️⃣ Définir les accès publics et sécuriser les autres routes
                .authorizeHttpRequests(auth -> auth
                        // Accès public
                        .requestMatchers(
                                "/", "/index.html",
                                "/auth/**",
                                "/css/**", "/js/**", "/images/**",
                                "/templates/fragments/**",
                                "/h2-console/**",
                                "/favicon.ico"
                        ).permitAll()
                        // API sécurisée par JWT
                        .requestMatchers("/api/**").authenticated()
                        // Toutes les autres requêtes nécessitent authentification
                        .anyRequest().authenticated()
                )

                // 3️⃣ Gestion de session
                // Pour les API REST, on utilise STATELESS pour JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // 4️⃣ Configuration login formulaire (web)
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("email")
                        .passwordParameter("motDePasse")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )

                // 5️⃣ Déconnexion
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout")
                        .permitAll()
                )

                // 6️⃣ Filtre JWT avant UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // 7️⃣ H2 console : désactiver frame options pour console H2
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    // 🔑 Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 🔑 AuthenticationManager pour login formulaire
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
