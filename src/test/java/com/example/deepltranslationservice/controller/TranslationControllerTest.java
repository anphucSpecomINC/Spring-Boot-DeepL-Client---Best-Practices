package com.example.deepltranslationservice.controller;

import com.example.deepltranslationservice.dto.request.TranslationRequest;
import com.example.deepltranslationservice.service.WebClientDeepLService;
import com.example.deepltranslationservice.testdata.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("TranslationController Unit Tests")
class TranslationControllerTest {
    @Mock
    private WebClientDeepLService translationService;
    @InjectMocks
    private TranslationController translationController;
    private TranslationRequest request;

    @BeforeEach
    void setUp() {
        request = new TranslationRequest();
        request.setText(TestConstants.TEXT_TO_TRANSLATE);
        request.setTargetLang(TestConstants.TARGET_LANG);
    }

    @Test
    @DisplayName("givenValidRequest_whenTranslate_thenReturnTranslatedResponse")
    void givenValidRequest_whenTranslate_thenReturnTranslatedResponse() {
        given(translationService.translate(TestConstants.TEXT_TO_TRANSLATE, TestConstants.TARGET_LANG))
                .willReturn(TestConstants.TRANSLATED_TEXT);
        Map<String, String> response = translationController.translate(request);
        assertThat(response).containsEntry("translatedText", TestConstants.TRANSLATED_TEXT);
        verify(translationService).translate(TestConstants.TEXT_TO_TRANSLATE, TestConstants.TARGET_LANG);
    }
}
