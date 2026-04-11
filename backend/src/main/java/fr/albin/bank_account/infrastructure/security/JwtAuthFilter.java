package fr.albin.bank_account.infrastructure.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
/**
 * Filtre de sécurité qui intercepte chaque requête HTTP pour vérifier la présence d'un token JWT dans le header Authorization.
 * Si un token valide est trouvé, il authentifie l'utilisateur dans le contexte de sécurité de Spring Security.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter{

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Cette méthode est appelée pour chaque requête HTTP. Elle vérifie le header Authorization, extrait le token JWT,
     * valide le token et, si tout est correct, authentifie l'utilisateur dans le contexte de sécurité de Spring Security.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
         // 1. Récupérer le header Authorization
        String authHeader = request.getHeader("Authorization");

        // 2. Vérifier que le header commence par "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraire le token (après "Bearer ")
        String token = authHeader.substring(7);

        // 4. Extraire le username du token
        String username = jwtService.extractUsername(token);

        // 5. Si username valide et pas encore authentifié
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtService.isTokenValid(token, username)) {
                // 6. Dire à Spring Security que l'utilisateur est authentifié
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, null, List.of());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 7. Continuer la chaîne de filtres
        filterChain.doFilter(request, response);
    }
    
}
