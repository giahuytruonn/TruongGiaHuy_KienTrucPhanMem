package iuh.paymentservice;

import lombok.*;

/** Published to RabbitMQ after payment succeeds → consumed by ShippingService. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    private Long   orderId;
    private Long   paymentId;
    private double amount;
    private String status;
}
