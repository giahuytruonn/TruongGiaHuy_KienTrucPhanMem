package iuh.eventdriven.choreography;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║         EVENT CHOREOGRAPHY – Food Ordering Workflow         ║
 * ╠══════════════════════════════════════════════════════════════╣
 * ║  Không có orchestrator trung tâm.                           ║
 * ║  Mỗi service tự lắng nghe event và tự quyết định hành động. ║
 * ║                                                              ║
 * ║  Flow:                                                       ║
 * ║  Client                                                      ║
 * ║    → POST /food-orders                                       ║
 * ║      → FoodOrderService publish "food.order.placed"         ║
 * ║        → FoodPaymentListener consume → process payment      ║
 * ║          → publish "food.payment.done"                      ║
 * ║            → FoodDeliveryListener consume → confirm delivery ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
@SpringBootApplication
public class ChoreographyDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChoreographyDemoApplication.class, args);
    }
}
