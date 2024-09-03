package com.spot.good2travel.common.fcm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.spot.good2travel.common.fcm.dto.FcmMessageDto;
import com.spot.good2travel.common.fcm.dto.FcmRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class FcmService {


    @Value("${spring.fcm.url}")
    private String FCM_URL;
    private final ObjectMapper objectMapper;
    private final FcmRepository fcmRepository;

    public String sendMessageTo(FcmRequestDto fcmRequestDto) throws IOException{
        String message = makeMessage(fcmRequestDto.getFcmToken(), fcmRequestDto.getTitle(), fcmRequestDto.getBody(), fcmRequestDto.isValidateOnly());

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(FCM_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    private String makeMessage(String fcmToken, String title, String message, boolean validateOnly) throws JsonProcessingException {
        FcmMessageDto fcmMessage = FcmMessageDto.builder()
                        .message(FcmMessageDto.Message.builder()
                                .token(fcmToken)
                                .notification(FcmMessageDto.Notification.builder()
                                        .title(title)
                                        .body(message)
                                        .build())
                                .build()).validateOnly(validateOnly).build();

        log.info("[makeMessage] fcmToken : {}, title : {} , message : {}", fcmToken, title, message);
        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "fcm.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    @Transactional
    public String updateToken(FcmRequestDto.FcmUpdateDto fcmUpdateDto){
        //todo 유저 정보 가져오기
       Fcm fcm = fcmRepository.findFcmByUserId(1L);
       fcm.toUpdate(fcmUpdateDto.getFcmToken());
       return fcm.getFcmToken();
    }
}
