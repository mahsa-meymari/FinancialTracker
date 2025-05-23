package com.mahsa.user_service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Creating PasswordEncoder bean...");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring Standard SecurityFilterChain bean...");
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authz -> {
                logger.info("Setting up standard authorization rules...");
                authz
                    .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()
                    .requestMatchers("/api/users/login").permitAll()
                    .requestMatchers("/api/users/validate/**").permitAll()
                    .requestMatchers("/error").permitAll() // Ensure /error is permitted
                    .anyRequest().authenticated(); // All other requests need authentication
                }
            )
            .httpBasic(withDefaults()); // Use HTTP Basic for authenticated requests
        logger.info("Standard SecurityFilterChain configured.");
        return http.build();
    }
}