package com.spot.good2travel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class Good2travelApplication {
    public static void main(String[] args) {
        SpringApplication.run(Good2travelApplication.class, args);
    }
}

