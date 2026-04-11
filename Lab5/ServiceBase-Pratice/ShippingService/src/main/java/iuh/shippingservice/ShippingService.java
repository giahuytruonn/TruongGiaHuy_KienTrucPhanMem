package iuh.shippingservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShippingService {

    private final ShipmentRepository shipmentRepository;
    private final RestTemplate       restTemplate;

    /**
     * Triggered when RabbitMQ delivers a "payment.completed" message.
     * 1. Generate tracking number
     * 2. Save shipment record
     * 3. Update order status → SHIPPED via REST to OrderService
     */
    public void createShipment(PaymentEvent event) {
        String trackingNumber = "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Shipment shipment = new Shipment(
                null,
                event.getOrderId(),
                trackingNumber,
                "PREPARING",
                "123 Demo Street, HCM City"
        );
        shipmentRepository.save(shipment);
        log.info("[ShippingService] Shipment created: tracking={} for orderId={}", trackingNumber, event.getOrderId());

        // Notify OrderService
        try {
            restTemplate.put(
                    "http://localhost:8081/orders/" + event.getOrderId() + "/status?status=SHIPPED",
                    null
            );
            log.info("[ShippingService] Order {} status updated to SHIPPED", event.getOrderId());
        } catch (Exception ex) {
            log.warn("[ShippingService] Could not update order status: {}", ex.getMessage());
        }
    }

    public List<Shipment> getAllShipments() {
        return shipmentRepository.findAll();
    }
}
