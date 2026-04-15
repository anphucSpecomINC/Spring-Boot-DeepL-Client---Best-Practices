package com.example.deepltranslationservice.service;

import com.example.deepltranslationservice.exception.DeepLClientException;
import com.example.deepltranslationservice.properties.DeepLProperties;
import com.example.deepltranslationservice.testdata.DeepLResponseTestBuilder;
import com.example.deepltranslationservice.testdata.TestConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("WebClientDeepLService Unit Tests")
class WebClientDeepLServiceTest {
    private MockWebServer mockWebServer;
    private WebClientDeepLService deepLTranslationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String baseUrl = mockWebServer.url("/").toString();
        WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
        DeepLProperties properties = new DeepLProperties();
        properties.setApiKey(TestConstants.API_KEY);
        properties.setBaseUrl(baseUrl);
        deepLTranslationService = new WebClientDeepLService(webClient, properties);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Nested
    @DisplayName("Translate method tests")
    class TranslateTests {
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
            String result = deepLTranslationService.translate(TestConstants.TEXT_TO_TRANSLATE, TestConstants.TARGET_LANG);
            assertThat(result).isEqualTo(TestConstants.TRANSLATED_TEXT);
            var recordedRequest = mockWebServer.takeRequest();
            assertThat(recordedRequest.getPath()).isEqualTo("/translate");
            assertThat(recordedRequest.getHeader("Authorization")).isEqualTo("DeepL-Auth-Key " + TestConstants.API_KEY);
        }

        @Test
        @DisplayName("givenEmptyResponse_whenTranslate_thenThrowRuntimeException")
        void givenEmptyResponse_whenTranslate_thenThrowRuntimeException() throws Exception {
            var responseBody = DeepLResponseTestBuilder.aDeepLResponse().build();
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(HttpStatus.OK.value())
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setBody(objectMapper.writeValueAsString(responseBody)));
            assertThatThrownBy(() -> deepLTranslationService.translate(TestConstants.TEXT_TO_TRANSLATE, TestConstants.TARGET_LANG))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("DeepL response is empty");
        }

        @ParameterizedTest
        @ValueSource(ints = {400, 403, 404, 413, 429, 500, 504})
        @DisplayName("givenHttpError_whenTranslate_thenThrowDeepLClientException")
        void givenHttpError_whenTranslate_thenThrowDeepLClientException(int statusCode) {
            String errorMsg = "Mock Error Body for " + statusCode;
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(statusCode)
                    .setBody(errorMsg));
            assertThatThrownBy(() -> deepLTranslationService.translate(TestConstants.TEXT_TO_TRANSLATE, TestConstants.TARGET_LANG))
                    .isInstanceOf(DeepLClientException.class)
                    .satisfies(ex -> {
                        DeepLClientException clientEx = (DeepLClientException) ex;
                        assertThat(clientEx.getStatusCode().value()).isEqualTo(statusCode);
                        assertThat(clientEx.getMessage()).contains(errorMsg);
                    });
        }

        @Test
        @DisplayName("givenNetworkError_whenTranslate_thenThrowRuntimeException")
        void givenNetworkError_whenTranslate_thenThrowRuntimeException() {
            mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));
            assertThatThrownBy(() -> deepLTranslationService.translate(TestConstants.TEXT_TO_TRANSLATE, TestConstants.TARGET_LANG))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Unexpected error during DeepL translation");
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
            List<String> results = deepLTranslationService.translate(texts, TestConstants.TARGET_LANG);
            assertThat(results).containsExactly("Xin chào", "Thế giới");
        }
    }
}
