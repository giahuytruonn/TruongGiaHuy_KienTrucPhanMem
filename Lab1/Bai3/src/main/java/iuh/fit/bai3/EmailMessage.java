package iuh.fit.bai3;

import lombok.Data;

import java.io.Serializable;

@Data
public class EmailMessage implements Serializable {
    private String to;
    private Long orderId;

    // constructor
    public EmailMessage() {}

    public EmailMessage(String to, Long orderId) {
        this.to = to;
        this.orderId = orderId;
    }

    // getter/setter
}
