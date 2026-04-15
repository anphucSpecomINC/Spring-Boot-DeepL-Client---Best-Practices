# DeepL Translation Service Integration (Spring Boot)

[English Version](README.md)

Một giải pháp tích hợp DeepL API chuẩn hóa dành cho môi trường Production, minh họa cách thiết kế Service Layer bền vững, xử lý lỗi hệ thống ngoại vi và so sánh các phương thức HTTP Client trong Spring Boot.

## 1. Tổng quan (Overview)
Repository này cung cấp một cấu trúc hoàn chỉnh để tích hợp DeepL API vào dự án Spring Boot. Thay vì chỉ là một bản demo đơn giản, dự án tập trung vào việc hiện thực hóa các **Best Practices** khi làm việc với bên thứ ba, từ cấu hình timeout, quản lý lỗi tập trung đến việc tối ưu hóa khả năng bảo trì mã nguồn.

*   **Công nghệ sử dụng:** Spring Boot 4.0.5, Java 21.
*   **Phạm vi:** Tích hợp DeepL API, xử lý đồng bộ (Blocking) và bất đồng bộ (Non-blocking).

## 2. Mục tiêu kỹ thuật (Objectives)
Dự án được thiết kế nhằm giúp nhà phát triển:
*   Xây dựng kiến trúc Integration Layer tách biệt hoàn toàn với Business Logic.
*   Hiểu và áp dụng sự khác biệt giữa **RestTemplate** (Legacy/Blocking) và **WebClient** (Modern/Non-blocking).
*   Thiết lập chiến lược xử lý lỗi (Error Handling Strategy) chặt chẽ cho các API bên thứ ba.
*   Viết mã nguồn dễ đọc, dễ kiểm thử (Testability) với độ bao phủ > 80%.

## 3. Kiến trúc hệ thống (Architecture)

### 3.1 Luồng dữ liệu tổng thể
`Controller` (REST Endpoints) → `Service Layer` (Business Mapping) → `HTTP Client` (WebClient/RestTemplate) → `DeepL External API`.

### 3.2 Thành phần chính
*   **Service Layer:** Gồm `WebClientDeepLService` và `RestTemplateDeepLService`, đóng gói logic gọi API.
*   **Client Abstraction:** Cấu hình Client thông qua `WebClientConfig` và `RestTemplateConfig` với đầy đủ Timeout.
*   **Exception Layer:** Hệ thống phân loại lỗi riêng biệt (`DeepLClientException`) và xử lý tập trung qua `GlobalExceptionHandler`.
*   **Data Transfer Objects (DTO):** Chuẩn hóa dữ liệu đầu vào/đầu ra, sử dụng `SnakeCaseStrategy` tương thích với DeepL.

## 4. Quyết định thiết kế (Design Considerations)
*   **Separation of Concerns:** Tách biệt logic cấu hình Client, định nghĩa lỗi và logic dịch thuật.
*   **No Nested Logic:** Mã nguồn trong Service được tách thành các phương thức private nhỏ để tăng khả năng đọc.
*   **Configuration Management:** Toàn bộ thông số nhạy cảm (API Key, Base URL) được quản lý qua `ConfigurationProperties`.
*   **Surgical Error Mapping:** Ánh xạ chính xác trạng thái lỗi từ API bên ngoài sang Exception nội bộ.

## 5. So sánh kỹ thuật: WebClient vs RestTemplate

| Đặc tính | WebClient | RestTemplate |
| :--- | :--- | :--- |
| **Cơ chế** | Non-blocking (Reactive) | Blocking (Synchronous) |
| **Hiệu năng** | Cao, tối ưu tài nguyên thread | Giới hạn theo mô hình thread-per-request |
| **Độ phức tạp** | Cao hơn | Thấp, dễ tiếp cận |
| **Khuyến nghị** | Dùng cho hệ thống hiện đại, tải cao | Dùng cho bảo trì dự án cũ |

## 6. Cấu hình (Configuration)
Ví dụ cấu hình trong `application.yaml`:
```yaml
deepl:
  base-url: https://api-free.deepl.com/v2
  api-key: ${DEEPL_API_KEY}
```

## 7. API Usage (Cách sử dụng)

### Dịch văn bản đơn (WebClient)
`POST /api/v1/translations`
```json
{
  "text": "Hello world",
  "targetLang": "VI"
}
```

### Dịch hàng loạt (Bulk Translation)
`POST /api/v1/translations/bulk` hoặc `/api/v1/translations/rest/bulk`
```json
{
  "texts": ["Hello", "System Integration"],
  "targetLang": "VI"
}
```

## 8. Chiến lược xử lý lỗi (Error Handling Strategy)
Dự án áp dụng nguyên tắc xử lý lỗi 3 lớp:
1.  **Transport Level:** Cấu hình Connect/Read Timeout (5s/10s) để tránh treo ứng dụng.
2.  **API Status Level:** Sử dụng `ResponseErrorHandler` (RestTemplate) và `onStatus` (WebClient) để bắt lỗi 4xx, 5xx.
3.  **Global Mapping:** `GlobalExceptionHandler` chuyển đổi các lỗi kỹ thuật thành phản hồi JSON thân thiện với Client.

## 9. Nguyên tắc phong cách mã nguồn (Code Style Principles)
*   **Readability over Cleverness:** Mã nguồn rõ ràng là ưu tiên số 1.
*   **Naming Clarity:** Tên Service và phương thức phản ánh chính xác công nghệ.
*   **Compact Code:** Đã dọn dẹp comment thừa và dòng trống không cần thiết.

## 10. Giới hạn & Hướng mở rộng (Limitations & Extensions)

### Giới hạn hiện tại:
*   Chưa có cơ chế **Retry**.
*   Chưa tích hợp **Circuit Breaker**.
*   Chưa có lớp **Caching**.

### Hướng mở rộng đề xuất:
*   Tích hợp **Resilience4j**.
*   Sử dụng **Spring Cache** với Redis.
*   Thêm **Micrometer Metrics**.

## 11. Đối tượng hướng đến
*   Backend Developers muốn tìm hiểu cách tích hợp Third-party API chuẩn.
*   Spring Boot Developers cần so sánh thực tế WebClient và RestTemplate.

## 12. Tài liệu tham khảo (References)
*   [DeepL API Documentation](https://developers.deepl.com/api-reference/translate/request-translation) - Tài liệu kỹ thuật chính thức.
*   [DeepL Mock API](https://github.com/anphucSpecomINC/deepl-mock-api) - Server giả lập (Mock) để test các phản hồi của DeepL API mà không cần gọi API thật.

## 13. License
Dự án được phân phối dưới giấy phép **MIT**.
