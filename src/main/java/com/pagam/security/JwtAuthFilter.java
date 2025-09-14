package com.pagam.security;

import com.pagam.repository.UtilisateurRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends GenericFilter {

    private final JwtService jwtService;
    private final UtilisateurRepository userRepo;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI();

        // Ignorer les pages front, ressources statiques et WebSocket
        if (path.startsWith("/templates/fragments/dashboard") ||
                path.startsWith("/alertes") ||
                path.startsWith("/utilisateurs") ||
                path.startsWith("/templates/fragments/produits") ||
                path.startsWith("/templates/fragments/producteurs") ||
                path.startsWith("/templates/fragments/commandes") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/") ||
                path.startsWith("/ws/") ||
                path.startsWith("/auth/") ||
                path.startsWith("/api/auth/") ||
                path.startsWith("/h2-console/")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = req.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String email = jwtService.extractUsername(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var user = userRepo.findByEmail(email).orElse(null);
                if (user != null && jwtService.isTokenValid(token, email)) {
                    var auth = new UsernamePasswordAuthenticationToken(
                            new User(user.getEmail(), user.getMotDePasse(),
                                    java.util.List.of(() -> user.getRole().name())),
                            null,
                            java.util.List.of(() -> user.getRole().name())
                    );
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }

        chain.doFilter(request, response);
    }
}