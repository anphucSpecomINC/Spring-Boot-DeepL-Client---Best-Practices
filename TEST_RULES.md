# Quy tắc Viết Test (Testing Rules)

Dự án này sử dụng **JUnit 5**, **Mockito (BDD Style)**, **AssertJ** và **MockMvc** làm nền tảng cho việc kiểm thử.

## 1. Cấu trúc và Đặt tên (Naming & Structure)

- **Tên class test**: Phải kết thúc bằng `Test` (Ví dụ: `AuthServiceTest`).
- **Tên method test**: Tuân theo pattern `given[Context]_when[Action]_then[ExpectedResult]`.
    - Ví dụ: `givenValidUserRequest_whenRegister_thenReturnUserResponse`.
- **Mô tả**: Sử dụng `@DisplayName` cho cả class và method để mô tả mục đích bằng ngôn ngữ dễ hiểu.
- **Phân nhóm**: Sử dụng `@Nested` để nhóm các test case theo từng method hoặc chức năng cụ thể của class được test.

## 2. Phong cách Kiểm thử (Testing Style)

- Sử dụng mô hình **Arrange - Act - Assert** (hoặc **Given - When - Then**).
- Khuyến khích comment rõ các block `// Given`, `// When`, `// Then` trong method test.
- Sử dụng **BDDMockito** (`given(...).willReturn(...)`) thay vì Mockito truyền thống (`when(...).thenReturn(...)`).
- Sử dụng **AssertJ** cho các câu lệnh assertion để tăng khả năng đọc hiểu (ví dụ:
  `assertThat(result).isEqualTo(expected)`).

## 3. Quản lý Dữ liệu Test (Test Data Management)

- **Builder Pattern**: Luôn sử dụng Builder cho các thực thể (Entities) và DTOs trong test.
    - Các builder được đặt trong thư mục `src/test/java/.../testdata/`.
    - Ví dụ: `UserTestBuilder.aUser().withEmail("test@example.com").build()`.
- **Constants**: Sử dụng `TestConstants` để lưu trữ các giá trị dùng chung (Email hợp lệ, Token giả lập, v.v.).

## 4. Unit Test cho Service

- Sử dụng `@ExtendWith(MockitoExtension.class)`.
- Mock các phụ thuộc (dependencies) bằng `@Mock`.
- Inject class cần test bằng `@InjectMocks`.
- Kiểm tra cả trường hợp thành công và các trường hợp ngoại lệ (Exceptions).

## 5. Web Layer Test (Controller)

- Sử dụng `@WebMvcTest(YourController.class)`.
- Sử dụng `@AutoConfigureMockMvc(addFilters = false)` nếu muốn bỏ qua lớp bảo mật (Security Filters) khi không cần
  thiết.
- Sử dụng `@MockitoBean` (Spring Boot 3.4+) hoặc `@MockBean` để mock các service.
- Sử dụng `MockMvc` để thực hiện request và `jsonPath` để kiểm tra kết quả trả về.

## 6. Repository Test (Data Layer)

- Sử dụng `@DataJpaTest` và `@ActiveProfiles("test")`.
- Sử dụng `TestEntityManager` để chuẩn bị dữ liệu mẫu trong DB.
- Đảm bảo có `@EnableJpaAuditing` nếu thực thể có sử dụng Auditing (CreatedAt, UpdatedAt).

## 7. Quy tắc Verification

- Luôn verify xem các mock method có được gọi đúng số lần và đúng tham số hay không bằng
  `then(mock).should(times(n)).methodName(...)`.
- Sử dụng `then(mock).shouldHaveNoInteractions()` hoặc `should(never())` để đảm bảo các hành động không mong muốn không
  xảy ra khi có lỗi.

---
*Lưu ý: Luôn chạy toàn bộ test suite trước khi tạo Pull Request để đảm bảo không có regression.*
