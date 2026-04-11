# Lab 5 – Event Choreography vs Event Orchestration

> **Sinh viên:** Trương Gia Huy  
> **Môn:** Kiến Trúc Phần Mềm  
> **Chủ đề:** Event Choreography vs Orchestration – Workflow đặt đồ ăn

---

## 1. Tổng quan hai mô hình

### 1.1 Event Choreography (Vũ đạo sự kiện)

> Không có "người chỉ huy". Mỗi service tự lắng nghe sự kiện liên quan → tự xử lý → tự publish sự kiện tiếp theo.

```
╔══════════════════════════════════════════════════════════════════════╗
║               EVENT CHOREOGRAPHY – Food Ordering                    ║
╠══════════════════════════════════════════════════════════════════════╣
║                                                                      ║
║  Client ──POST──► FoodOrderService                                   ║
║                        │                                            ║
║                        │ publish "food.order.placed"                ║
║                        ▼                                            ║
║                   [RabbitMQ]                                         ║
║                        │                                            ║
║                        │ consume (tự nghe, không ai chỉ)            ║
║                        ▼                                            ║
║                  FoodPaymentService                                  ║
║                        │                                            ║
║                        │ publish "food.payment.done"                ║
║                        ▼                                            ║
║                   [RabbitMQ]                                         ║
║                        │                                            ║
║                        │ consume (tự nghe)                          ║
║                        ▼                                            ║
║                  FoodDeliveryService                                 ║
║                        │                                            ║
║                        │ publish "food.delivery.done"               ║
║                        ▼                                            ║
║                    (Saga done)                                       ║
╚══════════════════════════════════════════════════════════════════════╝
```

**Đặc điểm:** Mỗi service chỉ biết mình cần lắng nghe event gì và publish event gì. Không cần biết ai sẽ nhận event đó.

---

### 1.2 Event Orchestration (Điều phối sự kiện)

> Có 1 Orchestrator trung tâm điều phối toàn bộ luồng. Các Worker chỉ nhận lệnh và trả kết quả.

```
╔══════════════════════════════════════════════════════════════════════╗
║               EVENT ORCHESTRATION – Food Ordering                   ║
╠══════════════════════════════════════════════════════════════════════╣
║                                                                      ║
║  Client ──POST──► FoodOrchestrator                                   ║
║                        │                                            ║
║            ┌───────────┼───────────────────────────────┐           ║
║            │           │                               │           ║
║      Step 1│     Step 2│                         Step 3│           ║
║            │           │                               │           ║
║            ▼           ▼                               ▼           ║
║       [OrderWorker] [PaymentWorker]           [DeliveryWorker]     ║
║            │           │                               │           ║
║            └───────────┴───────────────────────────────┘           ║
║                        │                                            ║
║              (reply back to Orchestrator)                            ║
║                        │                                            ║
║              Orchestrator quyết định bước tiếp theo                 ║
╚══════════════════════════════════════════════════════════════════════╝
```

**Đặc điểm:** Orchestrator biết toàn bộ business logic. Worker chỉ làm 1 việc cụ thể và báo cáo kết quả.

---

## 2. Cấu trúc project

```
EventDriven-Pratice/
├── choreography-demo/          ← Spring Boot :8084
│   └── src/main/java/iuh/eventdriven/choreography/
│       ├── ChoreographyDemoApplication.java   Main class
│       ├── RabbitConfig.java                  Exchange/Queue config
│       ├── FoodOrderEvent.java                Event DTO
│       ├── FoodOrderService.java              Tạo order + publish event
│       ├── FoodOrderController.java           REST /food-orders
│       ├── FoodPaymentListener.java           Lắng nghe + xử lý payment
│       └── FoodDeliveryListener.java          Lắng nghe + xác nhận delivery
│
├── orchestration-demo/         ← Spring Boot :8085
│   └── src/main/java/iuh/eventdriven/orchestration/
│       ├── OrchestrationDemoApplication.java  Main class
│       ├── RabbitConfig.java                  Exchange/Queue config
│       ├── FoodSagaCommand.java               Command DTO
│       ├── FoodOrchestrator.java              Bộ não trung tâm
│       ├── FoodOrderController.java           REST /orchestrated-orders
│       ├── OrderWorker.java                   Worker nhận lệnh tạo order
│       ├── PaymentWorker.java                 Worker nhận lệnh payment
│       └── DeliveryWorker.java                Worker nhận lệnh delivery
│
└── README.md
```

---

## 3. Cách chạy

### Bước 1 – Start RabbitMQ
```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

### Bước 2 – Chạy Choreography Demo (:8084)
```bash
cd choreography-demo
./mvnw spring-boot:run
```

### Bước 3 – Test Choreography
```bash
curl -X POST http://localhost:8084/food-orders \
  -H "Content-Type: application/json" \
  -d '{"foodName":"Phở Bò","quantity":2,"unitPrice":35000,"customerName":"Huy"}'
```

**Console log (choreography):**
```
[OrderService - CHOREOGRAPHY] New order placed
  orderId=FO-A1B2C3, food=Phở Bò, qty=2, total=70000.0, customer=Huy
  Publishing event → queue: food.order.placed

[PaymentService - CHOREOGRAPHY] Received 'food.order.placed'
  orderId=FO-A1B2C3, food=Phở Bò, amount=70000.0
  Processing payment... (simulated 500ms)
  Payment successful! Publishing → queue: food.payment.done

[DeliveryService - CHOREOGRAPHY] Received 'food.payment.done'
  orderId=FO-A1B2C3, customer=Huy, food=Phở Bò
  Preparing delivery... (simulated 800ms)
  Delivery confirmed! orderId=FO-A1B2C3 → status=DELIVERED
```

### Bước 4 – Chạy Orchestration Demo (:8085)
```bash
cd orchestration-demo
./mvnw spring-boot:run
```

### Bước 5 – Test Orchestration
```bash
curl -X POST http://localhost:8085/orchestrated-orders \
  -H "Content-Type: application/json" \
  -d '{"foodName":"Bún Bò Huế","quantity":1,"unitPrice":45000,"customerName":"Minh"}'
```

**Console log (orchestration):**
```
[ORCHESTRATOR] Starting SAGA: SAGA-X1Y2Z3
  Step 1/3: Sending CREATE_ORDER command → OrderWorker

  [OrderWorker] Received CREATE_ORDER command: sagaId=SAGA-X1Y2Z3, food=Bún Bò Huế
  [OrderWorker] Order created → replying to Orchestrator

[ORCHESTRATOR] Received REPLY from OrderWorker
  Step 2/3: Sending PROCESS_PAYMENT command → PaymentWorker

  [PaymentWorker] Received PROCESS_PAYMENT command: sagaId=SAGA-X1Y2Z3, amount=45000.0
  [PaymentWorker] Payment processed → replying to Orchestrator

[ORCHESTRATOR] Received REPLY from PaymentWorker
  Step 3/3: Sending SCHEDULE_DELIVERY command → DeliveryWorker

  [DeliveryWorker] Received SCHEDULE_DELIVERY: sagaId=SAGA-X1Y2Z3, customer=Minh
  [DeliveryWorker] Delivery scheduled → replying to Orchestrator

[ORCHESTRATOR] Received REPLY from DeliveryWorker
  ✅ SAGA COMPLETED SUCCESSFULLY! All steps done.
```

---

## 4. So sánh Choreography vs Orchestration

| Tiêu chí                   | Choreography                                     | Orchestration                                       |
|----------------------------|--------------------------------------------------|-----------------------------------------------------|
| **Cơ chế**                 | Mỗi service tự lắng nghe event                  | Orchestrator trung tâm điều phối                    |
| **Coupling**               | Loose coupling – service không biết nhau        | Service biết Orchestrator nhưng không biết nhau     |
| **Visibility**             | Khó trace – flow phân tán nhiều service         | Dễ trace – flow tập trung tại Orchestrator          |
| **Single Point of Failure**| Không có                                        | Orchestrator là SPOF                                |
| **Business Logic**         | Phân tán – mỗi service giữ 1 phần              | Tập trung – Orchestrator giữ toàn bộ logic         |
| **Complexity**             | Đơn giản từng service, phức tạp khi debug      | Orchestrator phức tạp, service đơn giản             |
| **Scalability**            | Dễ scale từng service                           | Scale Orchestrator là bottleneck                    |
| **Error Handling**         | Khó – phải implement compensation mỗi service  | Dễ – Orchestrator quyết định compensation          |
| **Testing**                | Khó test end-to-end                            | Dễ test – mock từng worker                          |
| **Team autonomy**          | Cao – team tự quản lý service của mình         | Thấp – phải phối hợp qua Orchestrator              |

---

## 5. Quyết định mô hình phù hợp với Scaling + Resilience

### Chọn Choreography khi:
- Hệ thống cần **high scalability** – mỗi service scale độc lập
- Số lượng service ít (3-5), flow đơn giản
- Team lớn, mỗi team sở hữu 1 service, muốn **tự chủ hoàn toàn**
- Không cần central visibility (hoặc có monitoring tools tốt như Jaeger, Zipkin)
- Resilience: không có SPOF, 1 service lỗi → service khác vẫn hoạt động

### Chọn Orchestration khi:
- Business logic **phức tạp** với nhiều điều kiện rẽ nhánh
- Cần **rollback/compensation** khi có lỗi (Saga pattern)
- Cần **audit trail** rõ ràng – ai làm gì, khi nào
- Team nhỏ, cần một nơi duy nhất để hiểu flow
- Cần dễ dàng **modify workflow** mà không ảnh hưởng service khác

### Kết luận cho bài toán đặt đồ ăn:

| Yêu cầu                          | Phù hợp với        |
|----------------------------------|---------------------|
| Scaling nhanh (nhiều đơn hàng)   | **Choreography**    |
| Xử lý lỗi phức tạp (hoàn tiền)  | **Orchestration**   |
| Team lớn, nhiều domain           | **Choreography**    |
| Audit log cho compliance         | **Orchestration**   |
| Resilience (không SPOF)          | **Choreography**    |

**Khuyến nghị:** Với **scaling** → Choreography. Với **resilience + error handling phức tạp** → Orchestration. Trong thực tế, nhiều hệ thống dùng **hybrid** (choreography giữa domain, orchestration trong domain).
