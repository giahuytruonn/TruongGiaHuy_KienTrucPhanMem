package iuh._2698251_truonggiahuy_lab01_bai1.service;

import iuh._2698251_truonggiahuy_lab01_bai1.configuration.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(String message) {
        System.out.println("[PRODUCER] Gửi: " + message);
        rabbitTemplate.convertAndSend(
                RabbitConfig.MAIN_EXCHANGE,
                "main.key",
                message
        );
    }
}
