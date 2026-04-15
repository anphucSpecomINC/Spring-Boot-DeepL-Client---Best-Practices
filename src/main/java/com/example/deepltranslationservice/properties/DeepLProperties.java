package com.example.deepltranslationservice.properties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "deepl")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeepLProperties {
    String baseUrl;
    String apiKey;
}
