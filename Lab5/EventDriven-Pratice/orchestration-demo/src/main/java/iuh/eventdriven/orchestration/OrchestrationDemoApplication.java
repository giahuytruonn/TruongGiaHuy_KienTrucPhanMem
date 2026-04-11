package iuh.eventdriven.orchestration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║       EVENT ORCHESTRATION – Food Ordering Workflow          ║
 * ╠══════════════════════════════════════════════════════════════╣
 * ║  Có 1 Orchestrator trung tâm điều phối toàn bộ luồng.      ║
 * ║  Các worker (Order/Payment/Delivery) KHÔNG tự biết          ║
 * ║  phải làm gì tiếp theo – chỉ nhận lệnh từ Orchestrator.    ║
 * ║                                                              ║
 * ║  Flow:                                                       ║
 * ║  Client                                                      ║
 * ║    → POST /orchestrated-orders                               ║
 * ║      → Orchestrator gửi lệnh → OrderWorker                  ║
 * ║        ← OrderWorker trả kết quả                            ║
 * ║      → Orchestrator gửi lệnh → PaymentWorker                ║
 * ║        ← PaymentWorker trả kết quả                          ║
 * ║      → Orchestrator gửi lệnh → DeliveryWorker               ║
 * ║        ← DeliveryWorker trả kết quả                         ║
 * ║      → Orchestrator kết thúc saga                           ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
@SpringBootApplication
public class OrchestrationDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrchestrationDemoApplication.class, args);
    }
}
