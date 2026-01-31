import factory.PaymentFactory;
import service.Payment;

public class Main {
    public static void main(String[] args) {
        Payment payment = PaymentFactory.createPayment("MOMO");
        if(payment!= null) {
            payment.process();
        }
        Payment payment1 = PaymentFactory.createPayment("VIETQR");
        if (payment1 != null) {
            payment1.process();
        }
    }
}