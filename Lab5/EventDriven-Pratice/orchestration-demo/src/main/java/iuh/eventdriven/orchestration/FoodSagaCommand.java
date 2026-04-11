package iuh.eventdriven.orchestration;

import lombok.*;

/** Command DTO sent by Orchestrator to each Worker. */
@Data @NoArgsConstructor @AllArgsConstructor
public class FoodSagaCommand {
    private String sagaId;
    private String foodName;
    private int    quantity;
    private double totalPrice;
    private String customerName;
    private String currentStep;   // ORDER | PAYMENT | DELIVERY
    private String status;        // PENDING | SUCCESS | FAILED
}
