# Lab 5 – Service-Based Architecture + Event-Driven (RabbitMQ)

> **Sinh viên:** Trương Gia Huy  
> **Môn:** Kiến Trúc Phần Mềm  

---

## 1. Tổng quan kiến trúc

```
                      ┌─────────────────────────────────────────────────┐
                      │              Shared Database                     │
                      │          MariaDB: service_base_db                │
                      │    [orders]  [payments]  [shipments]             │
                      └──────────────┬──────────────────────────────────┘
                                     │ (tất cả service dùng chung 1 DB)
         ┌────────────────────────────┼────────────────────────────────────┐
         │                           │                                    │
   ┌─────▼──────┐             ┌──────▼──────┐                    ┌───────▼──────┐
   │ OrderService│             │PaymentService│                    │ShippingService│
   │  :8081      │             │   :8082      │                    │    :8083      │
   └─────┬───────┘             └──────┬───────┘                    └───────┬───────┘
         │                           │                                    │
         │  publish "order.created"  │ publish "payment.completed"        │
         ▼                           ▼                                    │
   ┌───────────────────────────────────────────────────────────────────┐  │
   │                       RabbitMQ Message Broker                     │  │
   │   Exchange: order.exchange     Exchange: payment.exchange          │  │
   │   Queue: order.created  ──────►  PaymentService consumes         │  │
   │   Queue: payment.completed ────────────────────────────────────► │  │
   └───────────────────────────────────────────────────────────────────┘  │
                                                                           │
                                                              ShippingService consumes
```

### Luồng xử lý (Event Flow)

```
Client
  │── POST /orders ──► OrderService
                          │ 1. Lưu Order(status=PENDING) vào DB
                          │ 2. Publish "order.created" → RabbitMQ
                          ▼
                     [RabbitMQ: order.created queue]
                          │
                          ▼ (consume)
                     PaymentService
                          │ 3. Nhận event, lưu Payment(status=PROCESSING)
                          │ 4. Xử lý thanh toán → Payment(status=PAID)
                          │ 5. REST PUT /orders/{id}/status?status=PAID
                          │ 6. Publish "payment.completed" → RabbitMQ
                          ▼
                     [RabbitMQ: payment.completed queue]
                          │
                          ▼ (consume)
                     ShippingService
                          │ 7. Nhận event, tạo Shipment + tracking number
                          │ 8. REST PUT /orders/{id}/status?status=SHIPPED
```

---

## 2. Cấu trúc project

```
ServiceBase-Pratice/
├── OrderService/          ← Spring Boot :8081
│   └── src/main/java/iuh/orderservice/
│       ├── Order.java               Entity – bảng orders
│       ├── OrderRepository.java     JPA Repository
│       ├── OrderService.java        Business logic
│       ├── OrderController.java     REST API
│       ├── OrderProducer.java       RabbitMQ publisher
│       ├── OrderEvent.java          DTO truyền qua MQ
│       └── RabbitMQConfig.java      Cấu hình Exchange/Queue/Binding
│
├── PaymentService/        ← Spring Boot :8082
│   └── src/main/java/iuh/paymentservice/
│       ├── Payment.java             Entity – bảng payments
│       ├── PaymentRepository.java
│       ├── PaymentService.java      Business logic
│       ├── PaymentController.java   REST API
│       ├── PaymentConsumer.java     RabbitMQ consumer (order.created)
│       ├── PaymentProducer.java     RabbitMQ publisher
│       ├── OrderEvent.java          DTO nhận từ OrderService
│       ├── PaymentEvent.java        DTO gửi cho ShippingService
│       └── RabbitMQConfig.java
│
├── ShippingService/       ← Spring Boot :8083
│   └── src/main/java/iuh/shippingservice/
│       ├── Shipment.java            Entity – bảng shipments
│       ├── ShipmentRepository.java
│       ├── ShippingService.java     Business logic
│       ├── ShippingController.java  REST API
│       ├── ShippingConsumer.java    RabbitMQ consumer (payment.completed)
│       ├── PaymentEvent.java        DTO nhận từ PaymentService
│       └── RabbitMQConfig.java
│
├── frontend/
│   └── index.html         ← Giao diện FE (vanilla HTML/JS)
│
├── db/
│   └── init.sql           ← Script tạo DB + bảng
│
└── README.md              ← File này
```

---

## 3. Yêu cầu cài đặt

| Tool        | Phiên bản  |
|-------------|------------|
| Java JDK    | 21+        |
| Maven       | 3.9+       |
| MariaDB     | 10.6+      |
| RabbitMQ    | 3.x        |
| IntelliJ IDEA | Any      |

### Cài RabbitMQ (Windows – Docker)
```bash
docker run -d --name rabbitmq \
  -p 5672:5672 -p 15672:15672 \
  rabbitmq:3-management
```
> Management UI: http://localhost:15672 (guest/guest)

---

## 4. Cách chạy

### Bước 1 – Tạo database
```sql
-- Chạy file db/init.sql trong MariaDB
source /path/to/db/init.sql;
```

### Bước 2 – Start RabbitMQ
```bash
docker start rabbitmq
```

### Bước 3 – Chạy từng service (mở 3 terminal hoặc 3 IntelliJ run config)

```bash
# Terminal 1 – OrderService
cd OrderService && ./mvnw spring-boot:run

# Terminal 2 – PaymentService
cd PaymentService && ./mvnw spring-boot:run

# Terminal 3 – ShippingService
cd ShippingService && ./mvnw spring-boot:run
```

### Bước 4 – Mở Frontend
Mở file `frontend/index.html` trong trình duyệt (không cần web server).

---

## 5. REST API

### OrderService (:8081)
| Method | Endpoint                        | Mô tả                              |
|--------|---------------------------------|------------------------------------|
| POST   | `/orders`                       | Tạo đơn mới + publish to RabbitMQ |
| GET    | `/orders`                       | Lấy danh sách đơn hàng            |
| GET    | `/orders/{id}`                  | Lấy chi tiết đơn hàng             |
| PUT    | `/orders/{id}/status?status=X`  | Cập nhật trạng thái (nội bộ)      |

**Ví dụ tạo đơn:**
```json
POST http://localhost:8081/orders
Content-Type: application/json

{
  "productName": "Pizza Margherita",
  "quantity": 2,
  "price": 50000
}
```

**Response:**
```json
{
  "id": 1,
  "productName": "Pizza Margherita",
  "quantity": 2,
  "price": 50000.0,
  "status": "PENDING"
}
```

### PaymentService (:8082)
| Method | Endpoint     | Mô tả                  |
|--------|--------------|------------------------|
| GET    | `/payments`  | Lấy danh sách payments |

### ShippingService (:8083)
| Method | Endpoint      | Mô tả                   |
|--------|---------------|-------------------------|
| GET    | `/shipments`  | Lấy danh sách shipments |

---

## 6. Minh chứng chạy hệ thống

### Sequence khi POST /orders:

1. **OrderService log:**
   ```
   [OrderService] Order saved: id=1, product=Pizza Margherita, status=PENDING
   [OrderProducer] >>> Sending OrderCreated event to RabbitMQ: ...
   [OrderProducer] >>> Message sent successfully for orderId=1
   ```

2. **PaymentService log:**
   ```
   [PaymentConsumer] <<< Received OrderCreated event: orderId=1, product=Pizza Margherita
   [PaymentService] Processing payment id=1 for orderId=1, amount=100000.0
   [PaymentService] Payment PAID for orderId=1
   [PaymentService] Order 1 status updated to PAID
   [PaymentProducer] >>> Sending PaymentCompleted event to RabbitMQ
   ```

3. **ShippingService log:**
   ```
   [ShippingConsumer] <<< Received PaymentCompleted event: orderId=1, amount=100000.0
   [ShippingService] Shipment created: tracking=TRK-A1B2C3D4 for orderId=1
   [ShippingService] Order 1 status updated to SHIPPED
   ```

4. **DB sau khi hoàn thành:**
   ```
   orders:    id=1, status=SHIPPED
   payments:  id=1, orderId=1, status=PAID
   shipments: id=1, orderId=1, tracking=TRK-..., status=PREPARING
   ```

---

## 7. RabbitMQ Exchanges & Queues

| Exchange           | Type  | Queue               | Routing Key         | Consumer        |
|--------------------|-------|---------------------|---------------------|-----------------|
| `order.exchange`   | Topic | `order.created`     | `order.created`     | PaymentService  |
| `payment.exchange` | Topic | `payment.completed` | `payment.completed` | ShippingService |

---

## 8. Ưu điểm của Service-Based Architecture

| Tiêu chí         | Monolithic          | Service-Based (Lab này)         |
|------------------|---------------------|---------------------------------|
| Deploy           | 1 artifact          | 3 service riêng biệt            |
| Team phân công   | Khó                 | Mỗi team owns 1 service         |
| Scale            | Scale cả app        | Scale từng service độc lập      |
| Coupling         | Cao                 | Thấp (giao tiếp qua MQ)         |
| DB               | 1 DB chung          | 1 DB chung (trade-off)          |
| Fault isolation  | 1 lỗi → cả app down | 1 service lỗi → ít ảnh hưởng   |
