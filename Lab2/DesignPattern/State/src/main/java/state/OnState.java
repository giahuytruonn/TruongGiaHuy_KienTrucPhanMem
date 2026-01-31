package state;

public class OnState implements State {
    @Override
    public void turnOn() {
        System.out.println("Bóng đèn đã bật rồi");
    }

    @Override
    public void turnOff() {
        System.out.println("Tắt bóng đèn");
    }
}
