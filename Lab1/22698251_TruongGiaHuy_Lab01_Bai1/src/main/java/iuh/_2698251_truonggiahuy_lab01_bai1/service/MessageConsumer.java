package iuh._2698251_truonggiahuy_lab01_bai1.service;

import iuh._2698251_truonggiahuy_lab01_bai1.configuration.RabbitConfig;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {

    @RabbitListener(queues = RabbitConfig.MAIN_QUEUE)
    public void handleMainQueue(String message) {
        System.out.println("[CONSUMER] Nhận: " + message);

        if (message.contains("fail")) {
            System.err.println(" ❌ Lỗi nghiệp vụ! Chuyển sang DLQ...");
            throw new AmqpRejectAndDontRequeueException("Business Error");
        }

        System.out.println(" ✅ Xử lý thành công.");
    }

    @RabbitListener(queues = RabbitConfig.DLQ_QUEUE)
    public void handleDLQ(String message) {
        System.err.println("[DLQ] Message lỗi được lưu: " + message);
    }
}
