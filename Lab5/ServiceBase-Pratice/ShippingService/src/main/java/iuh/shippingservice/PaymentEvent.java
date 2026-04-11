package iuh.shippingservice;

import lombok.*;

/** Mirror of PaymentService's PaymentEvent – received from RabbitMQ. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    private Long   orderId;
    private Long   paymentId;
    private double amount;
    private String status;
}
