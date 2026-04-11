package iuh.eventdriven.orchestration;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/orchestrated-orders")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FoodOrderController {

    private final FoodOrchestrator orchestrator;

    /**
     * POST /orchestrated-orders
     * Body: { "foodName": "Bún Bò Huế", "quantity": 1, "unitPrice": 45000, "customerName": "Huy" }
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> placeOrder(@RequestBody Map<String, Object> body) {
        String foodName     = (String) body.get("foodName");
        int    quantity     = Integer.parseInt(body.get("quantity").toString());
        double unitPrice    = Double.parseDouble(body.get("unitPrice").toString());
        String customerName = (String) body.get("customerName");

        String sagaId = orchestrator.startSaga(foodName, quantity, unitPrice, customerName);

        return ResponseEntity.ok(Map.of(
                "pattern",     "EVENT ORCHESTRATION",
                "sagaId",      sagaId,
                "message",     "Saga started! Orchestrator is coordinating OrderWorker → PaymentWorker → DeliveryWorker",
                "description", "Central Orchestrator sends explicit commands. Workers reply back to orchestrator. " +
                               "Workers do NOT know what comes next."
        ));
    }
}
