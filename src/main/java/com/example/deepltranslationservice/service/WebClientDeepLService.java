package com.example.deepltranslationservice.service;

import com.example.deepltranslationservice.dto.request.DeepLRequest;
import com.example.deepltranslationservice.dto.response.DeepLResponse;
import com.example.deepltranslationservice.exception.DeepLClientException;
import com.example.deepltranslationservice.properties.DeepLProperties;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WebClientDeepLService {
    WebClient deepLWebClient;
    DeepLProperties deepLProperties;

    public String translate(String text, String targetLang) {
        return translate(List.of(text), targetLang).get(0);
    }

    public List<String> translate(List<String> texts, String targetLang) {
        DeepLRequest request = DeepLRequest.builder()
                .text(texts)
                .targetLang(targetLang)
                .build();
        DeepLResponse response = deepLWebClient.post()
                .uri("/translate")
                .header("Authorization", "DeepL-Auth-Key " + deepLProperties.getApiKey())
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleStatusError)
                .bodyToMono(DeepLResponse.class)
                .onErrorMap(this::isNotDeepLClientException, this::handleUnexpectedError)
                .block();
        if (response == null || response.getTranslations() == null || response.getTranslations().isEmpty()) {
            throw new RuntimeException("DeepL response is empty");
        }
        return response.getTranslations().stream()
                .map(com.example.deepltranslationservice.dto.response.Translation::getText)
                .toList();
    }

    private Mono<? extends Throwable> handleStatusError(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(String.class)
                .flatMap(errorBody -> {
                    log.error("DeepL API error: Status Code: {}, Error Body: {}", clientResponse.statusCode(), errorBody);
                    return Mono.error(new DeepLClientException(clientResponse.statusCode(), "DeepL API call failed: " + errorBody));
                });
    }

    private boolean isNotDeepLClientException(Throwable throwable) {
        return !(throwable instanceof DeepLClientException);
    }

    private Throwable handleUnexpectedError(Throwable throwable) {
        log.error("Unexpected error during DeepL API call: {}", throwable.getMessage());
        return new RuntimeException("Unexpected error during DeepL translation", throwable);
    }
}
