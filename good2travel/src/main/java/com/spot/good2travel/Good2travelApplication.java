package com.spot.good2travel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Good2travelApplication {
    public static void main(String[] args) {
        SpringApplication.run(Good2travelApplication.class, args);
    }
}

