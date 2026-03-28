# Báo Cáo Tổng Quan Codebase Lab 4

Tài liệu này cung cấp cái nhìn tổng quan về toàn bộ thư mục `Lab4`. Codebase phản ánh kiến thức về Docker cơ bản, ứng dụng container hóa các web framework khác nhau và minh họa sự khác biệt giữa hai kiến trúc phần mềm phổ biến: **Monolithic** (Nguyên khối) và **Microservices** (Vi dịch vụ) bằng **Spring Boot**.

## Cấu Trúc Thư Mục

Thư mục `Lab4` được chia thành 4 phần chính:

1. **`Docker01/`**: Chứa Dockerfile cơ bản mở đầu để làm quen với hệ sinh thái Docker.
2. **`Docker02/`**: Tổng hợp các Dockerfile thực hành việc dockerize (container hóa) cho nhiều môi trường và framework công nghệ đa dạng:
   - **Backend/Scripting**: Node.js, PHP, Go, Flask (Python).
   - **Frontend**: React (ứng dụng `my-react-app`).
   - **Database & Cache**: PostgreSQL, Redis.
   - **Web Server**: Nginx.
   - Các kĩ thuật nâng cao trong Docker như: sử dụng Environment Variable và Multi-stage Build.
3. **`monolithic/`**: Ứng dụng Backend được thiết kế theo kiến trúc nguyên khối.
4. **`microservice/`**: Ứng dụng Backend được thiết kế theo kiến trúc vi dịch vụ.

---

## Phân Tích Chi Tiết Các Kiến Trúc Ứng Dụng

Cả hai dự án `monolithic` và `microservice` đều được xây dựng dựa trên ngăn xếp công nghệ:
- Ngoại ngữ/Framework: **Java 17**, **Spring Boot 3.2.0**.
- Bảo mật: **Spring Security**, **JWT (jjwt v0.11.5)**.
- Cơ sở dữ liệu: **MySQL 8.0**, ORM qua **Spring Data JPA**.

### 1. Kiến Trúc Monolithic (`/monolithic`)

Kiến trúc này gói gọn mọi chức năng (Xác thực người dùng, Quản lý tài khoản) vào trong một ứng dụng duy nhất.
Tiến trình cài đặt và chạy hệ thống rất đơn giản bằng file `docker-compose.yml`.

- **Cấu trúc thành phần cốt lõi:**
  - **Controllers**: `AuthController`, `UserController` cung cấp API cho Client.
  - **Security**: Cấu hình Spring Security kết hợp với `JwtAuthenticationFilter` và `JwtService` để tạo/xác thực Json Web Token.
  - **Services**: `AuthService`, `UserDetailsServiceImpl` xử lý logic đăng nhập, đăng ký và load người dùng.
- **Docker & Orchestration (\`docker-compose.yml\`):**
  - Chạy 1 container `auth-mysql` (Database `authdb`, cổng 3306).
  - Chạy 1 container `auth-app` (App Spring Boot, cổng 8080).

### 2. Kiến Trúc Microservices (`/microservice`)

Kiến trúc này phân tách ứng dụng thành quản lý phân tán với nhiều cụm dịch vụ nhỏ độc lập. Cách tiếp cận này giúp dễ dàng mở rộng, bảo trì và phân tán rủi ro.

- **Các Dịch Vụ Thành Phần:**
  - **`api-gateway` (Port 8080)**: Cửa ngõ trung gian (API Gateway) dẫn hướng tất cả các request của ứng dụng, tích hợp JWT Filter (`JwtAuthFilter.java`) làm chốt chặn bảo mật xác thực Token cho toàn bộ cụm.
  - **`login-service` (Port 8082)**: Microservice đảm nhiệm độc lập tính năng đăng nhập, ký phát JWT (`JwtService`) và truy xuất thông tin User.
  - **`register-service` (Port 8081)**: Microservice đảm nhiệm tính năng đăng ký, sử dụng `LoginServiceClient` để giao tiếp nội bộ hoặc ủy quyền tương tác đến `login-service`.
- **Docker & Orchestration (\`docker-compose.yml\`):**
  Môi trường được orchestration phức tạp hơn với các container riêng rẽ liên kết qua mạng nội bộ `microservice-network`:
  - **2 Database Containers**: `mysql-login` (Port 3307) và `mysql-register` (Port 3308) chứa các DB chuyên biệt.
  - **3 Service Containers**: `login-service`, `register-service`, `api-gateway`.
  - Quản lý phụ thuộc logic (Depends On) kết hợp Healthchecks giúp đảm bảo DB được khởi tạo trước khi các Microservices sẵn sàng.

---

## Tổng Kết

Lab này là một minh chứng hoàn chỉnh cho lộ trình chuyển đổi và so sánh giữa Monolithic và Microservices. Thêm vào đó, việc sử dụng Docker / Docker Compose một cách triệt để không chỉ giúp đóng gói môi trường nhất quán mà còn cung cấp cơ sở vững chắc cho các khái niệm về triển khai phần mềm linh hoạt (Deployment / DevOps) trong thực tế.
