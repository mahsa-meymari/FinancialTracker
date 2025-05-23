package com.mahsa.transaction_service.security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User; // Simple UserDetails implementation
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList; // For creating an empty list of authorities

@Component
public class JwtRequestFilter extends OncePerRequestFilter { // Ensures this filter is executed once per request

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private JwtUtil jwtUtil; // Utility class for JWT operations (validation, claim extraction)

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Rxtract the Authorization header
        final String authorizationHeader = request.getHeader("Authorization");

        String jwtToken = null;
        Long userIdFromToken = null;
        String usernameFromToken = null;

        // 2. Check if the header is present and correctly formatted (starts with "Bearer ")
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7); // Extract the token part (after "Bearer ")

            try {
                // 3. Validate the token using JwtUtil
                // jwtUtil.validateToken() internally checks signature, expiration, and format,
                if (jwtUtil.validateToken(jwtToken)) {
                    // 4. If token is valid, extract userId and username claims
                    userIdFromToken = jwtUtil.extractUserId(jwtToken);
                    usernameFromToken = jwtUtil.extractUsername(jwtToken);
                } else {
                    // If validateToken returns false, it means an issue was found and logged by JwtUtil
                    // (e.g., expired, bad signature, malformed).
                    logger.warn("JWT Token validation failed (details logged by JwtUtil). Request to URI: {}", request.getRequestURI());
                }
            } catch (Exception e) {
                // This catch block is a safety net for any unexpected errors during token processing
                logger.error("Unexpected error processing JWT: {}. Token: [{}]", e.getMessage(), jwtToken);
            }
        } else {
            // No "Bearer " token found in the header. This is normal for unauthenticated requests
            // or if the client is not sending the token.
            // logger.trace("Authorization header missing or does not start with Bearer. Path: {}", request.getRequestURI());
        }

        // 5. If token was successfully validated and claims extracted, and no authentication is already set in context:
        if (userIdFromToken != null && usernameFromToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // UserDetails object. For services consuming JWTs (like this one),
            // Authorities (roles/permissions) would be extracted from the token if they were included.
            UserDetails userDetails = new User(usernameFromToken, "", new ArrayList<>()); // Empty password, empty authorities list

            // An Authentication object (UsernamePasswordAuthenticationToken is a common choice).
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userIdFromToken, // Principal (the authenticated user identifier)
                    null,            // Credentials (not needed as JWT is the credential)
                    userDetails.getAuthorities()); // Granted authorities (empty for now)

            // Set additional details of the authentication request
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set the Authentication object in the Spring SecurityContext.
            // This informs Spring Security that the current user is authenticated.
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            logger.debug("SecurityContext populated for User ID: {}. URI: {}", userIdFromToken, request.getRequestURI());
        }

        // 6. Continue the filter chain.
        // This allows the request to proceed to the next filter or, if this is the last relevant filter,
        // to the target controller/endpoint.
        filterChain.doFilter(request, response);
    }
}