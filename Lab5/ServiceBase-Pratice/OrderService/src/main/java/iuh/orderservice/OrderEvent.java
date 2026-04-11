package iuh.orderservice;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private Long orderId;
    private String productName;
    private int quantity;
    private double price;
    private String status;
}
