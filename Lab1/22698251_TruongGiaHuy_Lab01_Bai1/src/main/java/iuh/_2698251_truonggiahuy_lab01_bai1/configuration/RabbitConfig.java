package iuh._2698251_truonggiahuy_lab01_bai1.configuration;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String MAIN_EXCHANGE = "main.exchange";
    public static final String MAIN_QUEUE = "main.queue";
    public static final String DLX_EXCHANGE = "dlx.exchange";
    public static final String DLQ_QUEUE = "dlq.queue";

    // MAIN EXCHANGE
    @Bean
    DirectExchange mainExchange() {
        return new DirectExchange(MAIN_EXCHANGE);
    }

    // DLX
    @Bean
    DirectExchange dlxExchange() {
        return new DirectExchange(DLX_EXCHANGE);
    }

    // MAIN QUEUE (bind DLX)
    @Bean
    Queue mainQueue() {
        return QueueBuilder.durable(MAIN_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "dlq.key")
                .build();
    }

    // DLQ
    @Bean
    Queue dlqQueue() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }

    @Bean
    Binding mainBinding() {
        return BindingBuilder
                .bind(mainQueue())
                .to(mainExchange())
                .with("main.key");
    }

    @Bean
    Binding dlqBinding() {
        return BindingBuilder
                .bind(dlqQueue())
                .to(dlxExchange())
                .with("dlq.key");
    }
}
