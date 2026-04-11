package iuh.eventdriven.orchestration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * [ORCHESTRATION] – Central Orchestrator
 *
 * Đây là "bộ não" trung tâm. Nó biết:
 *   1. Bước 1: Gửi lệnh tạo order cho OrderWorker
 *   2. Khi OrderWorker xong → Gửi lệnh thanh toán cho PaymentWorker
 *   3. Khi PaymentWorker xong → Gửi lệnh giao hàng cho DeliveryWorker
 *   4. Khi DeliveryWorker xong → Saga hoàn thành
 *
 * Các Worker KHÔNG biết nhau và KHÔNG tự publish event tiếp theo.
 * Orchestrator hoàn toàn kiểm soát luồng.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class FoodOrchestrator {

    private final RabbitTemplate rabbitTemplate;

    /**
     * Entry point: Bắt đầu saga khi nhận yêu cầu từ REST controller.
     */
    public String startSaga(String foodName, int quantity, double unitPrice, String customerName) {
        String sagaId = "SAGA-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        FoodSagaCommand cmd = new FoodSagaCommand(
                sagaId, foodName, quantity, quantity * unitPrice, customerName, "ORDER", "PENDING"
        );

        log.info("╔══ [ORCHESTRATOR] Starting SAGA: {} ══════════════════════════", sagaId);
        log.info("║  Step 1/3: Sending CREATE_ORDER command → OrderWorker");
        log.info("╚═══════════════════════════════════════════════════════════════");

        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.CMD_CREATE_ORDER, cmd);
        return sagaId;
    }

    // ── Step 1 reply: OrderWorker → Orchestrator ─────────────────────────────
    @RabbitListener(queues = RabbitConfig.REPLY_ORDER_CREATED)
    public void onOrderCreated(FoodSagaCommand reply) {
        log.info("╔══ [ORCHESTRATOR] Received REPLY from OrderWorker ═══════════");
        log.info("║  sagaId={}, status={}", reply.getSagaId(), reply.getStatus());
        log.info("║  Step 2/3: Sending PROCESS_PAYMENT command → PaymentWorker");
        log.info("╚═══════════════════════════════════════════════════════════════");

        reply.setCurrentStep("PAYMENT");
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.CMD_PROCESS_PAYMENT, reply);
    }

    // ── Step 2 reply: PaymentWorker → Orchestrator ───────────────────────────
    @RabbitListener(queues = RabbitConfig.REPLY_PAYMENT_DONE)
    public void onPaymentDone(FoodSagaCommand reply) {
        log.info("╔══ [ORCHESTRATOR] Received REPLY from PaymentWorker ═════════");
        log.info("║  sagaId={}, status={}", reply.getSagaId(), reply.getStatus());
        log.info("║  Step 3/3: Sending SCHEDULE_DELIVERY command → DeliveryWorker");
        log.info("╚═══════════════════════════════════════════════════════════════");

        reply.setCurrentStep("DELIVERY");
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.CMD_SCHEDULE_DELIVERY, reply);
    }

    // ── Step 3 reply: DeliveryWorker → Orchestrator ──────────────────────────
    @RabbitListener(queues = RabbitConfig.REPLY_DELIVERY_DONE)
    public void onDeliveryDone(FoodSagaCommand reply) {
        log.info("╔══ [ORCHESTRATOR] Received REPLY from DeliveryWorker ════════");
        log.info("║  sagaId={}, customer={}, food={}", reply.getSagaId(), reply.getCustomerName(), reply.getFoodName());
        log.info("║  ✅ SAGA COMPLETED SUCCESSFULLY! All steps done.");
        log.info("╚═══════════════════════════════════════════════════════════════");
    }
}
