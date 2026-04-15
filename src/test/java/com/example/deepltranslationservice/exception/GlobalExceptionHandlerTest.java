package com.example.deepltranslationservice.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("givenDeepLClientException_whenHandle_thenReturnDeepLErrorResponse")
    void givenDeepLClientException_whenHandle_thenReturnDeepLErrorResponse() {
        // Given
        DeepLClientException ex = new DeepLClientException(HttpStatus.BAD_REQUEST, "DeepL error message");

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleDeepLClientException(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("error", "DeepL API Error");
        assertThat(response.getBody()).containsEntry("message", "DeepL error message");
        assertThat(response.getBody()).containsEntry("status", 400);
    }

    @Test
    @DisplayName("givenRuntimeException_whenHandle_thenReturnInternalServerErrorResponse")
    void givenRuntimeException_whenHandle_thenReturnInternalServerErrorResponse() {
        // Given
        RuntimeException ex = new RuntimeException("Runtime error message");

        // When
        ResponseEntity<Map<String, Object>> response = handler.handleRuntimeException(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsEntry("error", "Internal Server Error");
        assertThat(response.getBody()).containsEntry("message", "Runtime error message");
    }
}
