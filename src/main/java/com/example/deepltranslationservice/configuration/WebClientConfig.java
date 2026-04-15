package com.example.deepltranslationservice.configuration;

import com.example.deepltranslationservice.properties.DeepLProperties;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebClientConfig {
    DeepLProperties deepLProperties;

    public WebClient deepLWebClient() {
        return WebClient.builder()
                .baseUrl(deepLProperties.getBaseUrl())
                .build();
    }
}
