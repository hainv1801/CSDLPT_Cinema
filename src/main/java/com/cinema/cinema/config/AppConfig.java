package com.cinema.cinema.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    // Khai báo rõ ràng ObjectMapper để Spring tiêm vào
    // CustomAuthenticationEntryPoint
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    // THÊM BEAN NÀY ĐỂ GỌI API CHÉO
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
