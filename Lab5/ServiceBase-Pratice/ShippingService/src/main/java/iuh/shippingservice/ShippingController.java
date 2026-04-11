package iuh.shippingservice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shipments")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingService shippingService;

    /** GET /shipments  – list all shipment records */
    @GetMapping
    public ResponseEntity<List<Shipment>> getAllShipments() {
        return ResponseEntity.ok(shippingService.getAllShipments());
    }
}
