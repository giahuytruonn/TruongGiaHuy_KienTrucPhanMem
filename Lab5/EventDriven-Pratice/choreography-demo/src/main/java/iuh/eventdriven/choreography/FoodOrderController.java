package iuh.eventdriven.choreography;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/food-orders")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FoodOrderController {

    private final FoodOrderService foodOrderService;

    /**
     * POST /food-orders
     * Body: { "foodName": "Phở Bò", "quantity": 2, "unitPrice": 35000, "customerName": "Huy" }
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> placeOrder(@RequestBody Map<String, Object> body) {
        String foodName     = (String) body.get("foodName");
        int    quantity     = Integer.parseInt(body.get("quantity").toString());
        double unitPrice    = Double.parseDouble(body.get("unitPrice").toString());
        String customerName = (String) body.get("customerName");

        FoodOrderEvent event = foodOrderService.placeOrder(foodName, quantity, unitPrice, customerName);

        return ResponseEntity.ok(Map.of(
                "pattern",     "EVENT CHOREOGRAPHY",
                "message",     "Order placed! Event published to RabbitMQ. No central orchestrator.",
                "orderId",     event.getOrderId(),
                "status",      event.getStatus(),
                "description", "PaymentService will independently consume 'food.order.placed' " +
                               "→ then publish 'food.payment.done' " +
                               "→ DeliveryService will independently consume it"
        ));
    }
}
