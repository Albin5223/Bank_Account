package fr.albin.bank_account.infrastructure.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.albin.bank_account.domain.model.User;
import fr.albin.bank_account.domain.port.out.userPort.GenerateTokenPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
/**
 * Service pour la gestion des tokens JWT. Il permet de générer un token à partir du username, 
 * d'extraire le username d'un token et de valider un token.
 */
@Service
public class JwtService implements GenerateTokenPort{
    
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Override
    public String generateToken(User user) {
        return Jwts.builder()
        .subject(user.getUsername())        // qui est l'utilisateur
        .issuedAt(new Date())     // quand le token a été créé
        .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // expire dans 24h
        .signWith(getSigningKey()) // signature avec la clé secrète
        .compact();               // génère le token en String
    }

    //Récupérer la clé de signature à partir de la clé secrète
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //Extraire le username d'un token
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Valider un token
    public boolean isTokenValid(String token, String username) {
        String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
        return expiration.before(new Date());
    }
}
