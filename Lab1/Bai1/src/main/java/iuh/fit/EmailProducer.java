package iuh.fit;

import com.rabbitmq.client.*;


public class EmailProducer {

    private static final String HOST = "localhost";

    public static void main(String[] args) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(
                    RabbitMQConfig.NOTIFICATION_QUEUE,
                    true,
                    false,
                    false,
                    null
            );

            String recipient = "user@example.com";
            String subject = "Hệ thống thông báo";
            String body = "Chào mừng bạn đến với hệ thống RabbitMQ!";
            String message = String.format("%s|%s|%s", recipient, subject, body);

            channel.basicPublish(
                    "",
                    RabbitMQConfig.NOTIFICATION_QUEUE,
                    null,
                    message.getBytes()
            );

            System.out.println(" [x] Sent: '" + message + "'");
        }
    }
}
