package com.example.deepltranslationservice.service;

import com.example.deepltranslationservice.exception.DeepLClientException;
import com.example.deepltranslationservice.properties.DeepLProperties;
import com.example.deepltranslationservice.testdata.DeepLResponseTestBuilder;
import com.example.deepltranslationservice.testdata.TestConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("RestTemplateDeepLService Unit Tests")
class RestTemplateDeepLServiceTest {
    private MockWebServer mockWebServer;
    private RestTemplateDeepLService deepLRestTemplateService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = mockWebServer.url("/").toString();
        DeepLProperties properties = new DeepLProperties();
        properties.setApiKey(TestConstants.API_KEY);
        properties.setBaseUrl(baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl);
        deepLRestTemplateService = new RestTemplateDeepLService(new RestTemplate(), properties);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("givenValidRequest_whenTranslate_thenReturnTranslatedText")
    void givenValidRequest_whenTranslate_thenReturnTranslatedText() throws Exception {
        var responseBody = DeepLResponseTestBuilder.aDeepLResponse()
                .withTranslation(TestConstants.TRANSLATED_TEXT)
                .build();
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(responseBody)));
        String result = deepLRestTemplateService.translate(TestConstants.TEXT_TO_TRANSLATE, TestConstants.TARGET_LANG);
        assertThat(result).isEqualTo(TestConstants.TRANSLATED_TEXT);
    }

    @Test
    @DisplayName("givenValidBulkRequest_whenTranslate_thenReturnTranslatedTexts")
    void givenValidBulkRequest_whenTranslate_thenReturnTranslatedTexts() throws Exception {
        List<String> texts = List.of("Hello", "World");
        var responseBody = DeepLResponseTestBuilder.aDeepLResponse()
                .withTranslation("Xin chào")
                .withTranslation("Thế giới")
                .build();
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(objectMapper.writeValueAsString(responseBody)));
        List<String> results = deepLRestTemplateService.translate(texts, TestConstants.TARGET_LANG);
        assertThat(results).containsExactly("Xin chào", "Thế giới");
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 403, 429, 500})
    @DisplayName("givenHttpError_whenTranslate_thenThrowDeepLClientException")
    void givenHttpError_whenTranslate_thenThrowDeepLClientException(int statusCode) {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(statusCode)
                .setBody("Error from DeepL"));
        assertThatThrownBy(() -> deepLRestTemplateService.translate(TestConstants.TEXT_TO_TRANSLATE, TestConstants.TARGET_LANG))
                .isInstanceOf(DeepLClientException.class)
                .satisfies(ex -> {
                    DeepLClientException clientEx = (DeepLClientException) ex;
                    assertThat(clientEx.getStatusCode().value()).isEqualTo(statusCode);
                });
    }

    @Test
    @DisplayName("givenNetworkError_whenTranslate_thenThrowRuntimeException")
    void givenNetworkError_whenTranslate_thenThrowRuntimeException() {
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(okhttp3.mockwebserver.SocketPolicy.DISCONNECT_AT_START));
        assertThatThrownBy(() -> deepLRestTemplateService.translate(TestConstants.TEXT_TO_TRANSLATE, TestConstants.TARGET_LANG))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unexpected error during DeepL translation");
    }
}
