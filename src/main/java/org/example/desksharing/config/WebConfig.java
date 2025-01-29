package org.example.desksharing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Erlaubt alle Pfade
                        .allowedOrigins("http://localhost:8081") // Erlaubt Anfragen von Frontend
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // Erlaubt HTTP-Methoden
                        .allowedHeaders("*") // Erlaubt alle Header
                        .allowCredentials(true); // Erlaubt Cookies/Authorization-Headers
            }
        };
    }
}
