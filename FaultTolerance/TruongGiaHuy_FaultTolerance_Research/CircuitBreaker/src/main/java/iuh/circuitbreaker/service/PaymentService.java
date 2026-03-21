package iuh.circuitbreaker.service;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PaymentService.class);

    public String pay() {
        logger.info("PaymentService: Processing payment... (This should NOT appear if Circuit Breaker is OPEN)");
        // Giả lập gọi API bên thứ 3
        throw new RuntimeException("Payment Gateway timeout");
    }
}
