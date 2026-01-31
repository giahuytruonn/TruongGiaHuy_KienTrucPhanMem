package factory;

import model.Momo;
import model.VietQr;
import service.Payment;

import java.util.Locale;

public abstract class PaymentFactory {
    public static Payment createPayment(String type) {
        if(type == null) return null;
        return switch (type.toLowerCase()) {
            case "momo" -> new Momo();
            case "vietqr" -> new VietQr();
            default -> throw new IllegalArgumentException("Loai thanh toan khong hop le");
        };
    }
}
