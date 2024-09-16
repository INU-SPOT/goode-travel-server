package com.spot.good2travel.service;

import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.ImageReadException;
import com.spot.good2travel.common.exception.ImageSendException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${spring.image-import-url}")
    private String imageImportUrl;

    @Value("${spring.image-get-url}")
    private String imageGetUrl;

    private final WebClient webClient;

    public String uploadUserImageToNginx(MultipartFile file) {
        if (file == null) {
            return null;
        }
        String uniqueFilename = generateUniqueFilename(file);
        MultiValueMap<String, Object> body = createMultipartBody(file, uniqueFilename);
        uploadToServer(body);

        return uniqueFilename;
    }

    private String generateUniqueFilename(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = (originalFilename != null && originalFilename.contains("."))
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";

        return UUID.randomUUID() + extension;
    }

    private MultiValueMap<String, Object> createMultipartBody(MultipartFile file, String uniqueFilename) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return uniqueFilename;
                }
            });
            return body;
        } catch (IOException e) {
            throw new ImageReadException(ExceptionMessage.IMAGE_READ_ERROR);
        }
    }

    private String uploadToServer(MultiValueMap<String, Object> body) throws ImageSendException {
        return webClient.post()
                .uri(imageImportUrl)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                .body(BodyInserters.fromMultipartData(body))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String getImageUrl(String imageName){
        if(imageName == null || imageName.isEmpty()){
            return null;
        }

        return imageGetUrl + "/" + imageName;
    }
}
