package iuh.eventdriven.choreography;

import lombok.*;

/** Event published by FoodOrderService when a new food order is placed. */
@Data @NoArgsConstructor @AllArgsConstructor
public class FoodOrderEvent {
    private String orderId;
    private String foodName;
    private int    quantity;
    private double totalPrice;
    private String customerName;
    private String status;  // PLACED | PAYMENT_DONE | DELIVERED
}
