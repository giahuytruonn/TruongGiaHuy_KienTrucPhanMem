package iuh.fit.bai3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailProducer emailProducer;

    public Order createOrder(String email) {

        Order order = new Order();
        order.setCustomerEmail(email);
        order.setStatus("CREATED");

        orderRepository.save(order);

        // ❌ GỬI EMAIL TRỰC TIẾP
        emailService.sendOrderEmail(email, order.getId());

        return order;
    }

    public Order createOrderUseMQ(String email) {

        Order order = new Order();
        order.setCustomerEmail(email);
        order.setStatus("CREATED");

        orderRepository.save(order);

        // ✅ PUSH QUA QUEUE
        emailProducer.sendEmailMessage(
                new EmailMessage(email, order.getId())
        );

        return order;
    }
}
