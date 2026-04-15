package com.example.deepltranslationservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@Slf4j
public class DeepLRestTemplateErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        String errorBody = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
        log.error("DeepL API (RestTemplate) error: Method: {}, URL: {}, Status Code: {}, Error Body: {}",
                method, url, response.getStatusCode(), errorBody);
        throw new DeepLClientException(
                response.getStatusCode(),
                "DeepL API call failed (RestTemplate): " + errorBody
        );
    }
}
