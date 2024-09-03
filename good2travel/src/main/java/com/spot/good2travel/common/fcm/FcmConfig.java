package com.spot.good2travel.common.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Slf4j
@Configuration
public class FcmConfig {

    @PostConstruct
    public void initialize() {
        try{
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(
                            GoogleCredentials.fromStream(new ClassPathResource("fcm.json").getInputStream())
                    )
                    .build();
            FirebaseApp.initializeApp(options);
            log.info("FCM 설정 성공");
        } catch (IOException exception){
            log.error("Fcm 연결 오류 {}", exception.getMessage());
        }
    }
}
