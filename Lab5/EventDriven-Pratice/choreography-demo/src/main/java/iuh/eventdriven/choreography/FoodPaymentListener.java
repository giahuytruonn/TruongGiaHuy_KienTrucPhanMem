package iuh.eventdriven.choreography;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * [CHOREOGRAPHY] – FoodPaymentService (simulated as a listener component)
 *
 * Tự động lắng nghe "food.order.placed" mà KHÔNG cần orchestrator bảo.
 * Sau khi thanh toán xong → tự publish "food.payment.done".
 * KHÔNG biết DeliveryService tồn tại.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class FoodPaymentListener {

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitConfig.QUEUE_ORDER_PLACED)
    public void onOrderPlaced(FoodOrderEvent event) {
        log.info("╔══ [PaymentService - CHOREOGRAPHY] Received 'food.order.placed' ══");
        log.info("║  orderId={}, food={}, amount={}", event.getOrderId(), event.getFoodName(), event.getTotalPrice());
        log.info("║  Processing payment... (simulated 500ms)");

        simulateWork(500);

        event.setStatus("PAYMENT_DONE");
        log.info("║  Payment successful! Publishing → queue: {}", RabbitConfig.QUEUE_PAYMENT_DONE);
        log.info("╚════════════════════════════════════════════════════════════════");

        // Publish next event — DeliveryService will pick it up independently
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.QUEUE_PAYMENT_DONE, event);
    }

    private void simulateWork(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
