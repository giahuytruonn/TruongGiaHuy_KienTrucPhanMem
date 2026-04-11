package iuh.eventdriven.orchestration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * [ORCHESTRATION] – OrderWorker
 *
 * Chỉ biết: "Khi nhận lệnh CREATE_ORDER → thực thi → trả kết quả về cho Orchestrator."
 * KHÔNG biết bước tiếp theo là gì. Orchestrator quyết định.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderWorker {

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitConfig.CMD_CREATE_ORDER)
    public void handleCreateOrder(FoodSagaCommand cmd) {
        log.info("  [OrderWorker] Received CREATE_ORDER command: sagaId={}, food={}", cmd.getSagaId(), cmd.getFoodName());
        simulateWork(400);

        cmd.setStatus("SUCCESS");
        log.info("  [OrderWorker] Order created → replying to Orchestrator");

        // Reply to orchestrator (NOT to next service – that's orchestration!)
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.REPLY_ORDER_CREATED, cmd);
    }

    private void simulateWork(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
