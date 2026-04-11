package iuh.shippingservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ShippingConsumer {

    private final ShippingService shippingService;

    /**
     * Listens to "payment.completed" queue.
     * Triggered automatically by RabbitMQ when PaymentService confirms payment.
     */
    @RabbitListener(queues = "payment.completed")
    public void handlePaymentCompleted(PaymentEvent event) {
        log.info("[ShippingConsumer] <<< Received PaymentCompleted event: orderId={}, amount={}",
                event.getOrderId(), event.getAmount());
        shippingService.createShipment(event);
    }
}
