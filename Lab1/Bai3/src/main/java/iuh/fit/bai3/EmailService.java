package iuh.fit.bai3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOrderEmail(String toEmail, Long orderId) {
        try {
            // giả lập gửi chậm
            Thread.sleep(5000);

            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(toEmail);
            mail.setSubject("Xác nhận đơn hàng #" + orderId);
            mail.setText("Đơn hàng của bạn đã được tạo thành công!");

            mailSender.send(mail);
        } catch (Exception e) {
            throw new RuntimeException("Gửi email thất bại");
        }
    }
}
