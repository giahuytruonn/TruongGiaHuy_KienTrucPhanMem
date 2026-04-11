package iuh.orderservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProducer   orderProducer;

    /**
     * Create a new order → save as PENDING → publish OrderCreated event to RabbitMQ.
     */
    public Order createOrder(Order order) {
        order.setStatus("PENDING");
        Order saved = orderRepository.save(order);
        log.info("[OrderService] Order saved: id={}, product={}, status={}", saved.getId(), saved.getProductName(), saved.getStatus());

        OrderEvent event = new OrderEvent(
                saved.getId(),
                saved.getProductName(),
                saved.getQuantity(),
                saved.getPrice(),
                saved.getStatus()
        );
        orderProducer.sendOrderCreated(event);
        return saved;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    /**
     * Called by PaymentService / ShippingService via REST to update order status.
     */
    public Order updateOrderStatus(Long id, String status) {
        Order order = getOrderById(id);
        String oldStatus = order.getStatus();
        order.setStatus(status);
        Order updated = orderRepository.save(order);
        log.info("[OrderService] Order {} status changed: {} -> {}", id, oldStatus, status);
        return updated;
    }
}
