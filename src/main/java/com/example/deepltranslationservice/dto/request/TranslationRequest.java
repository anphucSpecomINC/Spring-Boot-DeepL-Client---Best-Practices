package com.example.deepltranslationservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TranslationRequest {
    String text;
    List<String> texts;
    @NotBlank(message = "Target language is required")
    String targetLang;
}
