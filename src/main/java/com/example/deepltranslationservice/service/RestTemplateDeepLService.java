package com.example.deepltranslationservice.service;

import com.example.deepltranslationservice.dto.request.DeepLRequest;
import com.example.deepltranslationservice.dto.response.DeepLResponse;
import com.example.deepltranslationservice.exception.DeepLRestTemplateErrorHandler;
import com.example.deepltranslationservice.properties.DeepLProperties;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RestTemplateDeepLService {
    RestTemplate restTemplate;
    DeepLProperties deepLProperties;

    public String translate(String text, String targetLang) {
        return translate(List.of(text), targetLang).get(0);
    }

    public List<String> translate(List<String> texts, String targetLang) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "DeepL-Auth-Key " + deepLProperties.getApiKey());
        DeepLRequest requestDto = DeepLRequest.builder()
                .text(texts)
                .targetLang(targetLang)
                .build();
        HttpEntity<DeepLRequest> entity = new HttpEntity<>(requestDto, headers);
        restTemplate.setErrorHandler(new DeepLRestTemplateErrorHandler());
        try {
            String url = deepLProperties.getBaseUrl() + "/translate";
            DeepLResponse response = restTemplate.postForObject(url, entity, DeepLResponse.class);
            if (response == null || response.getTranslations() == null || response.getTranslations().isEmpty()) {
                throw new RuntimeException("DeepL (RestTemplate) response is empty");
            }
            return response.getTranslations().stream()
                    .map(com.example.deepltranslationservice.dto.response.Translation::getText)
                    .toList();
        } catch (Exception e) {
            log.error("Unexpected error during DeepL translation (RestTemplate): {}", e.getMessage());
            if (e instanceof com.example.deepltranslationservice.exception.DeepLClientException) {
                throw e;
            }
            throw new RuntimeException("Unexpected error during DeepL translation", e);
        }
    }
}
