package iuh.paymentservice;

import lombok.*;

/** Mirror of OrderService's OrderEvent – shared via RabbitMQ message. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private Long   orderId;
    private String productName;
    private int    quantity;
    private double price;
    private String status;
}
