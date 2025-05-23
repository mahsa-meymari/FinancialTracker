package com.mahsa.transaction_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    // Inject the JWT secret string from application.properties.
    @Value("${jwt.secret}")
    private String secretString;

    // The SecretKey object derived from the secretString.
    private SecretKey key;

    // This method is called by Spring after the bean is constructed and properties are injected.
    // It initializes the SecretKey.
    @PostConstruct
    public void init() {
        try {
            // Converts the string secret into a SecretKey suitable for HMAC-SHA algorithms.
            // The key length should be appropriate for the algorithm (e.g., HS256 needs at least 256 bits / 32 bytes).
            this.key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            // If the secretString is too short or invalid for the algorithm,
            // hmacShaKeyFor can throw an exception (e.g., WeakKeyException).
            logger.error("!!! CRITICAL: Failed to initialize JWT SecretKey. The jwt.secret is likely too short or invalid for the chosen algorithm. Ensure it's strong and of sufficient length. !!!", e);
            // Prevent application startup if the key cannot be initialized by re-throwing a runtime exception:
            throw new RuntimeException("Failed to initialize JWT SecretKey due to invalid secret string", e);
        }
    }

    /**
     * Parses the JWT token, verifies its signature, and returns all claims.
     * This method will throw various exceptions if the token is invalid (e.g., expired, malformed, bad signature).
     *
     * @param token The JWT string.
     * @return The Claims object containing all data from the token payload.
     * @throws ExpiredJwtException if the token is expired.
     * @throws UnsupportedJwtException if the token's format is not supported.
     * @throws MalformedJwtException if the token is not a valid JWS.
     * @throws SignatureException if the token's signature validation fails.
     * @throws IllegalArgumentException if the token string is null, empty or only whitespace.
     */
    private Claims extractAllClaims(String token) {
        // Jwts.parser() creates a new JWT parser instance.
        // .verifyWith(key) sets the SecretKey to use for verifying the token's signature.
        // .build() finalizes the parser configuration.
        // .parseSignedClaims(token) parses the JWS string, verifies signature, checks expiration, etc.
        // .getPayload() returns the claims (payload) part of the token.
        return Jwts.parser()
                .verifyWith(key) 
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * A generic function to extract a specific claim from the token.
     *
     * @param token          The JWT string.
     * @param claimsResolver A function that takes Claims and returns the desired claim value.
     * @param <T>            The type of the claim to be extracted.
     * @return The extracted claim value.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts the username (expected to be the 'subject' of the token).
     *
     * @param token The JWT string.
     * @return The username (subject).
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the userId from the custom "userId" claim in the token.
     *
     * @param token The JWT string.
     * @return The Long userId.
     */
    public Long extractUserId(String token) {
        // extractAllClaims is called implicitly by extractClaim, or call it directly:
        final Claims claims = extractAllClaims(token);
        // Retrieves the "userId" claim and attempts to cast it to Long.
        // Ensure that user-service actually puts "userId" as a Long when creating the token.
        return claims.get("userId", Long.class);
    }

    /**
     * Extracts the expiration date from the token.
     *
     * @param token The JWT string.
     * @return The expiration Date.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Validates the JWT token.
     * It checks if the token can be parsed (which verifies signature) and is not expired.
     * Specific exceptions during parsing are caught and logged, then returns false.
     *
     * @param token The JWT string.
     * @return True if the token is valid and not expired, false otherwise.
     */
    public Boolean validateToken(String token) {
        try {
            // The act of parsing the claims will verify the signature and check standard claims like expiration.
            // If any of these fail, an exception (e.g., SignatureException, ExpiredJwtException) will be thrown.
            extractAllClaims(token);
            // If extractAllClaims doesn't throw an exception, the token is considered valid
            // in terms of signature, format, and standard claims like 'exp'.
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token (malformed): {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            // This is expected if the token is past its expiration date.
            logger.warn("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            // This can happen if the token string is null, empty, or only whitespace,
            // or if claims are missing when expected by specific getters.
            logger.error("JWT claims string is empty or argument is invalid: {}", e.getMessage());
        } catch (Exception e) {
            // Catch-all for any other unexpected exceptions during parsing.
            logger.error("Unexpected error validating JWT token: {}", e.getMessage(), e);
        }
        return false; // If any exception was caught, the token is considered invalid.
    }
}