package iuh.shippingservice;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "shipments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long   orderId;
    private String trackingNumber;
    private String status;    // PREPARING | IN_TRANSIT | DELIVERED
    private String address;
}
