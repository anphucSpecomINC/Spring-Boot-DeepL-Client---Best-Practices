# DeepL Translation Service Integration (Spring Boot)

[Phiên bản Tiếng Việt](README_VI.md)

A production-oriented integration solution for DeepL API, showcasing robust service layer design, external error handling, and a comparison of modern HTTP clients in Spring Boot.

## 1. Overview
This repository provides a complete structure for integrating the DeepL API into a Spring Boot application. Beyond a simple demo, it focuses on implementing **Best Practices** for third-party integrations, including timeout configurations, centralized error management, and maintainable code patterns.

*   **Tech Stack:** Spring Boot 4.0.5, Java 21.
*   **Scope:** DeepL API integration, supporting both Blocking and Non-blocking paradigms.

## 2. Objectives
This project is designed to help developers:
*   Build an Integration Layer isolated from Business Logic.
*   Compare and contrast **RestTemplate** (Legacy/Blocking) vs. **WebClient** (Modern/Non-blocking).
*   Implement a rigorous Error Handling Strategy for third-party APIs.
*   Write clean, testable code with >80% coverage.

## 3. Architecture

### 3.1 Overall Flow
`Controller` (REST Endpoints) → `Service Layer` (Business Mapping) → `HTTP Client` (WebClient/RestTemplate) → `DeepL External API`.

### 3.2 Key Components
*   **Service Layer:** Contains `WebClientDeepLService` and `RestTemplateDeepLService` encapsulating API logic.
*   **Client Abstraction:** Configured via `WebClientConfig` and `RestTemplateConfig` with dedicated Timeouts.
*   **Exception Layer:** Custom exception types (`DeepLClientException`) handled centrally via `GlobalExceptionHandler`.
*   **Data Transfer Objects (DTO):** Standardized input/output models with `SnakeCaseStrategy`.

## 4. Design Considerations
*   **Separation of Concerns:** Clear boundaries between configuration, error handling, and translation logic.
*   **No Nested Logic:** Service methods are refactored into small, private helper methods (e.g., `handleStatusError`) for readability.
*   **Configuration Management:** Sensitive data (API Keys, URLs) managed via `ConfigurationProperties`.
*   **Surgical Error Mapping:** Precise mapping of external API statuses to internal exceptions.

## 5. Technical Comparison: WebClient vs RestTemplate

| Feature | WebClient | RestTemplate |
| :--- | :--- | :--- |
| **Mechanism** | Non-blocking (Reactive) | Blocking (Synchronous) |
| **Performance** | High, optimized thread usage | Limited by thread-per-request model |
| **Complexity** | Higher (Requires Reactor knowledge) | Low, easy to learn |
| **Recommendation** | Modern, high-scale systems | Legacy maintenance or simple logic |

## 6. Configuration
Example `application.yaml`:
```yaml
deepl:
  base-url: https://api-free.deepl.com/v2
  api-key: ${DEEPL_API_KEY}
```

## 7. API Usage

### Single Text Translation (WebClient)
`POST /api/v1/translations`
```json
{
  "text": "Hello world",
  "targetLang": "VI"
}
```

### Bulk Translation
`POST /api/v1/translations/bulk` or `/api/v1/translations/rest/bulk`
```json
{
  "texts": ["Hello", "System Integration"],
  "targetLang": "VI"
}
```

## 8. Error Handling Strategy
The project follows a 3-layer error handling principle:
1.  **Transport Level:** Connect/Read Timeouts (5s/10s) to prevent system hangs.
2.  **API Status Level:** `ResponseErrorHandler` (RestTemplate) and `onStatus` (WebClient) to catch 4xx and 5xx errors.
3.  **Global Mapping:** `GlobalExceptionHandler` converts technical errors into client-friendly JSON responses.

## 9. Code Style Principles
*   **Readability over Cleverness:** Clear code is always favored over complex "hacks".
*   **Naming Clarity:** Service names explicitly reflect the underlying technology (e.g., `WebClientDeepLService`).
*   **Compact Code:** Minimized boilerplate, comments, and blank lines for core logic focus.

## 10. Limitations & Possible Extensions

### Current Limitations:
*   No **Retry** mechanism for transient failures.
*   No **Circuit Breaker** (e.g., Resilience4j) for fault tolerance.
*   No **Caching** layer for duplicate requests.

### Possible Extensions:
*   Integrate **Resilience4j** for retries and circuit breaking.
*   Implement **Spring Cache** with Redis for results.
*   Add **Micrometer Metrics** for API performance monitoring.

## 11. Target Audience
*   Backend Developers learning professional third-party API integration.
*   Spring Boot Developers comparing WebClient and RestTemplate in a real-world scenario.

## 12. License
Distributed under the **MIT License**.
