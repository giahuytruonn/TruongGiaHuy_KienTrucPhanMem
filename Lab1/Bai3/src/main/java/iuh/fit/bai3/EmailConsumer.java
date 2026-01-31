package iuh.fit.bai3;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailConsumer {

    @Autowired
    private JavaMailSender mailSender;

    @RabbitListener(queues = RabbitConfig.ORDER_NOTIFICATION_QUEUE)
    public void processOrderNotification(EmailMessage message) throws InterruptedException {

        // Simulate processing delay
        Thread.sleep(3000);

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(message.getTo());
        mail.setSubject("Thông báo đơn hàng #" + message.getOrderId());
        mail.setText("Cảm ơn bạn đã đặt hàng. Đơn hàng #" + message.getOrderId() + " đang được chuẩn bị!");

        mailSender.send(mail);

        System.out.println(">>> Notification sent for Order ID: " + message.getOrderId());
    }
}
