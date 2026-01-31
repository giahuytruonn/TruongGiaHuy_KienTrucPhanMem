package state;

public class OffState implements State {
    @Override
    public void turnOn() {
        System.out.println("Bật bóng đèn");
    }

    @Override
    public void turnOff() {
        System.out.println("Bóng đèn đã tắt rồi");
    }
}
