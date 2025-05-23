package com.mahsa.user_service.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.mahsa.user_service.entity.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretString;

    @Value("${jwt.expiration.ms}")
    private long expirationTimeMs;

    private SecretKey key;
    
    // Initialize the key
    @jakarta.annotation.PostConstruct // Ensures this method is called after dependency injection is done
    public void init() {
        // For HS256, key length must be at least 256 bits (32 bytes)
        this.key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        // You can add custom claims relevant to your application
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTimeMs);

        return Jwts.builder()
                .claims(claims) // Set custom claims
                .subject(user.getUsername()) // Subject of the token (often username or userId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key) // signs with the appropriate algorithm for the SecretKey 
                .compact();
    }




}
