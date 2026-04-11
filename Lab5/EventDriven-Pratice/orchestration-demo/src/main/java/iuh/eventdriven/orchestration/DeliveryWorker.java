package iuh.eventdriven.orchestration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * [ORCHESTRATION] – DeliveryWorker
 *
 * Chỉ biết: "Khi nhận lệnh SCHEDULE_DELIVERY → thực thi → trả kết quả về cho Orchestrator."
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DeliveryWorker {

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitConfig.CMD_SCHEDULE_DELIVERY)
    public void handleScheduleDelivery(FoodSagaCommand cmd) {
        log.info("  [DeliveryWorker] Received SCHEDULE_DELIVERY: sagaId={}, customer={}", cmd.getSagaId(), cmd.getCustomerName());
        simulateWork(600);

        cmd.setStatus("SUCCESS");
        log.info("  [DeliveryWorker] Delivery scheduled → replying to Orchestrator");

        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.REPLY_DELIVERY_DONE, cmd);
    }

    private void simulateWork(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
