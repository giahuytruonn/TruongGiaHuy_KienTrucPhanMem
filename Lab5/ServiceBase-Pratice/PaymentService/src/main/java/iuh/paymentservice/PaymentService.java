package iuh.paymentservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentProducer   paymentProducer;
    private final RestTemplate      restTemplate;

    /**
     * Triggered when RabbitMQ delivers an "order.created" message.
     * 1. Save payment record (PROCESSING)
     * 2. Simulate processing (always succeeds in demo)
     * 3. Update order status → PAID via REST
     * 4. Publish "payment.completed" event → ShippingService
     */
    public void processPayment(OrderEvent event) {
        double totalAmount = event.getPrice() * event.getQuantity();
        Payment payment = new Payment(null, event.getOrderId(), totalAmount, "PROCESSING");
        payment = paymentRepository.save(payment);
        log.info("[PaymentService] Processing payment id={} for orderId={}, amount={}",
                payment.getId(), event.getOrderId(), totalAmount);

        // Simulate payment gateway call
        payment.setStatus("PAID");
        paymentRepository.save(payment);
        log.info("[PaymentService] Payment PAID for orderId={}", event.getOrderId());

        // Notify OrderService via REST
        try {
            restTemplate.put(
                    "http://localhost:8081/orders/" + event.getOrderId() + "/status?status=PAID",
                    null
            );
            log.info("[PaymentService] Order {} status updated to PAID", event.getOrderId());
        } catch (Exception ex) {
            log.warn("[PaymentService] Could not update order status: {}", ex.getMessage());
        }

        // Publish next event → ShippingService
        PaymentEvent paymentEvent = new PaymentEvent(event.getOrderId(), payment.getId(), totalAmount, "PAID");
        paymentProducer.sendPaymentCompleted(paymentEvent);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
