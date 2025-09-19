package com.ciicc.Banking_Application.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    // Base64-encoded 256-bit key (persistent)
    private final String SECRET = "V1JhY29tbWVuZGVkU2VjdXJlS2V5Rm9ySkdXVEVTVC0xMjM0NTY=";

    private SecretKey key;

    private final long EXPIRATION_MS = 1000 * 60 * 60 * 24; // 24 hours

    @PostConstruct
    public void init() {
        // Decode the Base64 secret to get a SecretKey
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET));
    }

    // Generate JWT token from user email
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract email (subject) from token
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Validate token only
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ✅ Overload validateToken: check both validity and user identity
    public boolean validateToken(String token, UserDetails userDetails) {
        String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && validateToken(token));
    }
}
