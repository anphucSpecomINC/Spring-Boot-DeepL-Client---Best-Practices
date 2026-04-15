package com.example.deepltranslationservice.controller;

import com.example.deepltranslationservice.dto.request.TranslationRequest;
import com.example.deepltranslationservice.service.RestTemplateDeepLService;
import com.example.deepltranslationservice.service.WebClientDeepLService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/translations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TranslationController {
    WebClientDeepLService translationService;
    RestTemplateDeepLService restTemplateService;

    @PostMapping
    public Map<String, String> translate(@RequestBody @Valid TranslationRequest request) {
        String result = translationService.translate(request.getText(), request.getTargetLang());
        return Map.of("translatedText", result);
    }

    @PostMapping("/bulk")
    public Map<String, List<String>> translateBulk(@RequestBody @Valid TranslationRequest request) {
        List<String> results = translationService.translate(request.getTexts(), request.getTargetLang());
        return Map.of("translatedTexts", results);
    }

    @PostMapping("/rest/bulk")
    public Map<String, List<String>> translateBulkWithRestTemplate(@RequestBody @Valid TranslationRequest request) {
        List<String> results = restTemplateService.translate(request.getTexts(), request.getTargetLang());
        return Map.of("translatedTexts", results);
    }
}
