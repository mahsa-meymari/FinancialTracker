package com.mahsa.transaction_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    //This makes a RestTemplate instance available for injection anywhere in transaction-service.
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
