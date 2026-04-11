package iuh.paymentservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentConsumer {

    private final PaymentService paymentService;

    /**
     * Listens to "order.created" queue.
     * Triggered automatically by RabbitMQ when OrderService places a new order.
     */
    @RabbitListener(queues = "order.created")
    public void handleOrderCreated(OrderEvent event) {
        log.info("[PaymentConsumer] <<< Received OrderCreated event: orderId={}, product={}",
                event.getOrderId(), event.getProductName());
        paymentService.processPayment(event);
    }
}
