package iuh.eventdriven.orchestration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * [ORCHESTRATION] – PaymentWorker
 *
 * Chỉ biết: "Khi nhận lệnh PROCESS_PAYMENT → thực thi → trả kết quả về cho Orchestrator."
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentWorker {

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitConfig.CMD_PROCESS_PAYMENT)
    public void handleProcessPayment(FoodSagaCommand cmd) {
        log.info("  [PaymentWorker] Received PROCESS_PAYMENT command: sagaId={}, amount={}", cmd.getSagaId(), cmd.getTotalPrice());
        simulateWork(500);

        cmd.setStatus("SUCCESS");
        log.info("  [PaymentWorker] Payment processed → replying to Orchestrator");

        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.REPLY_PAYMENT_DONE, cmd);
    }

    private void simulateWork(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
