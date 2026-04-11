package iuh.eventdriven.orchestration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // Orchestrator → Workers (commands)
    public static final String CMD_CREATE_ORDER   = "orch.cmd.create.order";
    public static final String CMD_PROCESS_PAYMENT = "orch.cmd.process.payment";
    public static final String CMD_SCHEDULE_DELIVERY = "orch.cmd.schedule.delivery";

    // Workers → Orchestrator (replies)
    public static final String REPLY_ORDER_CREATED    = "orch.reply.order.created";
    public static final String REPLY_PAYMENT_DONE     = "orch.reply.payment.done";
    public static final String REPLY_DELIVERY_DONE    = "orch.reply.delivery.done";

    public static final String EXCHANGE = "food.orchestration.exchange";

    @Bean public TopicExchange orchExchange() { return new TopicExchange(EXCHANGE); }

    // Command queues
    @Bean public Queue cmdCreateOrder()       { return new Queue(CMD_CREATE_ORDER,       true); }
    @Bean public Queue cmdProcessPayment()    { return new Queue(CMD_PROCESS_PAYMENT,    true); }
    @Bean public Queue cmdScheduleDelivery()  { return new Queue(CMD_SCHEDULE_DELIVERY,  true); }

    // Reply queues
    @Bean public Queue replyOrderCreated()    { return new Queue(REPLY_ORDER_CREATED,    true); }
    @Bean public Queue replyPaymentDone()     { return new Queue(REPLY_PAYMENT_DONE,     true); }
    @Bean public Queue replyDeliveryDone()    { return new Queue(REPLY_DELIVERY_DONE,    true); }

    // Bindings
    @Bean Binding bCmdOrder(TopicExchange orchExchange)    { return BindingBuilder.bind(cmdCreateOrder()).to(orchExchange).with(CMD_CREATE_ORDER); }
    @Bean Binding bCmdPayment(TopicExchange orchExchange)  { return BindingBuilder.bind(cmdProcessPayment()).to(orchExchange).with(CMD_PROCESS_PAYMENT); }
    @Bean Binding bCmdDelivery(TopicExchange orchExchange) { return BindingBuilder.bind(cmdScheduleDelivery()).to(orchExchange).with(CMD_SCHEDULE_DELIVERY); }
    @Bean Binding bRepOrder(TopicExchange orchExchange)    { return BindingBuilder.bind(replyOrderCreated()).to(orchExchange).with(REPLY_ORDER_CREATED); }
    @Bean Binding bRepPayment(TopicExchange orchExchange)  { return BindingBuilder.bind(replyPaymentDone()).to(orchExchange).with(REPLY_PAYMENT_DONE); }
    @Bean Binding bRepDelivery(TopicExchange orchExchange) { return BindingBuilder.bind(replyDeliveryDone()).to(orchExchange).with(REPLY_DELIVERY_DONE); }

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
