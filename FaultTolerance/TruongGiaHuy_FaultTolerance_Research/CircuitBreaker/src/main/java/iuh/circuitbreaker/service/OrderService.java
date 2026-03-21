package iuh.circuitbreaker.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(OrderService.class);

    private final PaymentService paymentService;

    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @CircuitBreaker(
            name = "paymentCircuitBreaker",
            fallbackMethod = "paymentFallback"
    )
    public ResponseEntity<String> checkout() {
        logger.info("OrderService: Received checkout request. Calling PaymentService...");
        String result = paymentService.pay();
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<String> paymentFallback(Throwable ex) {
        if (ex instanceof io.github.resilience4j.circuitbreaker.CallNotPermittedException) {
             logger.error("OrderService: Circuit Breaker is OPEN! Request blocked immediately.");
        } else {
             logger.warn("OrderService: Payment failed, fallback triggered. Reason: " + ex.getMessage());
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Thanh toán tạm thời không khả dụng. Vui lòng thử lại sau.");
    }
}