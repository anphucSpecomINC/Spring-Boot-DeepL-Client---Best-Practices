package com.example.deepltranslationservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(DeepLClientException.class)
    public ResponseEntity<Map<String, Object>> handleDeepLClientException(DeepLClientException ex) {
        log.error("Handling DeepLClientException: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of(
                        "error", "DeepL API Error",
                        "message", ex.getMessage(),
                        "status", ex.getStatusCode().value()
                ));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        log.error("Handling RuntimeException: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError()
                .body(Map.of(
                        "error", "Internal Server Error",
                        "message", ex.getMessage()
                ));
    }
}
