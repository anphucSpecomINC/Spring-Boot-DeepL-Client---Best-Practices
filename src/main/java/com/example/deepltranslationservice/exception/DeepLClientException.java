package com.example.deepltranslationservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class DeepLClientException extends RuntimeException {
    private final HttpStatusCode statusCode;

    public DeepLClientException(HttpStatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
}
