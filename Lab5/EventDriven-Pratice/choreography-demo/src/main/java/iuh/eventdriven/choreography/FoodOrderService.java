package iuh.eventdriven.choreography;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * [CHOREOGRAPHY] – FoodOrderService
 *
 * Nhiệm vụ: Nhận yêu cầu đặt đồ ăn → publish event "food.order.placed".
 * KHÔNG biết PaymentService hay DeliveryService tồn tại.
 * Chỉ quan tâm: "Tôi đặt xong → tôi publish event → ai cần thì tự lắng nghe."
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FoodOrderService {

    private final RabbitTemplate rabbitTemplate;

    public FoodOrderEvent placeOrder(String foodName, int quantity, double unitPrice, String customerName) {
        String orderId = "FO-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        double total   = quantity * unitPrice;

        FoodOrderEvent event = new FoodOrderEvent(orderId, foodName, quantity, total, customerName, "PLACED");

        log.info("╔══ [OrderService - CHOREOGRAPHY] New order placed ══════════");
        log.info("║  orderId={}, food={}, qty={}, total={}, customer={}",
                orderId, foodName, quantity, total, customerName);
        log.info("║  Publishing event → queue: {}", RabbitConfig.QUEUE_ORDER_PLACED);
        log.info("╚══════════════════════════════════════════════════════════");

        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.QUEUE_ORDER_PLACED, event);
        return event;
    }
}
