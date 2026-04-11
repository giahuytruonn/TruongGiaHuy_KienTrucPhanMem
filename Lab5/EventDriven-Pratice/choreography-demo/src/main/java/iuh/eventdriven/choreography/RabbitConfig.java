package iuh.eventdriven.choreography;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // ── Queue names ───────────────────────────────────────────
    public static final String QUEUE_ORDER_PLACED   = "food.order.placed";
    public static final String QUEUE_PAYMENT_DONE   = "food.payment.done";
    public static final String QUEUE_DELIVERY_DONE  = "food.delivery.done";

    // ── Exchange ───────────────────────────────────────────────
    public static final String EXCHANGE = "food.choreography.exchange";

    @Bean
    public TopicExchange choreographyExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean Queue orderPlacedQueue()  { return new Queue(QUEUE_ORDER_PLACED,  true); }
    @Bean Queue paymentDoneQueue()  { return new Queue(QUEUE_PAYMENT_DONE,  true); }
    @Bean Queue deliveryDoneQueue() { return new Queue(QUEUE_DELIVERY_DONE, true); }

    @Bean
    public Binding orderPlacedBinding(TopicExchange choreographyExchange) {
        return BindingBuilder.bind(orderPlacedQueue()).to(choreographyExchange).with(QUEUE_ORDER_PLACED);
    }
    @Bean
    public Binding paymentDoneBinding(TopicExchange choreographyExchange) {
        return BindingBuilder.bind(paymentDoneQueue()).to(choreographyExchange).with(QUEUE_PAYMENT_DONE);
    }
    @Bean
    public Binding deliveryDoneBinding(TopicExchange choreographyExchange) {
        return BindingBuilder.bind(deliveryDoneQueue()).to(choreographyExchange).with(QUEUE_DELIVERY_DONE);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
        RabbitTemplate t = new RabbitTemplate(cf);
        t.setMessageConverter(messageConverter());
        return t;
    }
}
