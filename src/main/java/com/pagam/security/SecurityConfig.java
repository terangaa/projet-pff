package com.pagam.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.concurrent.atomic.AtomicReference;

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
                        .requestMatchers("/", "/home/**","/services/**","/about/**",         // <-- ajouté
                                "/contact/**","/auth/**", "/css/**", "/js/**", "/images/**",
                                "/templates/fragments/**", "/h2-console/**", "/favicon.ico").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                )

                // 3️⃣ Gestion de session
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // 4️⃣ Formulaire de login avec redirection par rôle
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("email")
                        .passwordParameter("motDePasse")
                        .successHandler(authenticationSuccessHandler())
                        .failureUrl("/auth/login?error=true")
                        .permitAll()
                )

                // 5️⃣ Déconnexion
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/")
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

    // 🔹 Redirection automatique selon rôle après login
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
            AtomicReference<String> redirectUrl = new AtomicReference<>("/dashboard"); // valeur par défaut

            authentication.getAuthorities().forEach(authority -> {
                switch (authority.getAuthority()) {
                    case "ADMIN" -> redirectUrl.set("/dashboard"); // ou /admin-home
                    case "AGRICULTEUR" -> redirectUrl.set("/dashboard"); // ou /agriculteur-home
                    case "ACHETEUR" -> redirectUrl.set("/dashboard"); // ou /acheteur-home
                    case "DECIDEUR" -> redirectUrl.set("/dashboard"); // ou /decideur-home
                }
            });

            response.sendRedirect(redirectUrl.get());
        };
    }
}
