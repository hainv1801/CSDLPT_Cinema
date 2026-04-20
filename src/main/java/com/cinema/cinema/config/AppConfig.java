package com.cinema.cinema.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    // Khai báo rõ ràng ObjectMapper để Spring tiêm vào
    // CustomAuthenticationEntryPoint
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
