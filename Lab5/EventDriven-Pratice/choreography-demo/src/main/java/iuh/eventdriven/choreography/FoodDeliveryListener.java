package iuh.eventdriven.choreography;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * [CHOREOGRAPHY] – FoodDeliveryService (simulated as a listener component)
 *
 * Tự động lắng nghe "food.payment.done" mà KHÔNG cần orchestrator bảo.
 * Sau khi giao hàng → publish "food.delivery.done" (saga kết thúc).
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class FoodDeliveryListener {

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitConfig.QUEUE_PAYMENT_DONE)
    public void onPaymentDone(FoodOrderEvent event) {
        log.info("╔══ [DeliveryService - CHOREOGRAPHY] Received 'food.payment.done' ══");
        log.info("║  orderId={}, customer={}, food={}", event.getOrderId(), event.getCustomerName(), event.getFoodName());
        log.info("║  Preparing delivery... (simulated 800ms)");

        simulateWork(800);

        event.setStatus("DELIVERED");
        log.info("║  Delivery confirmed! orderId={} → status=DELIVERED", event.getOrderId());
        log.info("║  Publishing → queue: {}", RabbitConfig.QUEUE_DELIVERY_DONE);
        log.info("╚════════════════════════════════════════════════════════════════");

        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.QUEUE_DELIVERY_DONE, event);
    }

    private void simulateWork(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
