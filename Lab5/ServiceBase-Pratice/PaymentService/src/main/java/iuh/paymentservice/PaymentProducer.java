package iuh.paymentservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendPaymentCompleted(PaymentEvent event) {
        log.info("[PaymentProducer] >>> Sending PaymentCompleted event to RabbitMQ: {}", event);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.PAYMENT_COMPLETED_ROUTING,
                event
        );
        log.info("[PaymentProducer] >>> Message sent for orderId={}", event.getOrderId());
    }
}
