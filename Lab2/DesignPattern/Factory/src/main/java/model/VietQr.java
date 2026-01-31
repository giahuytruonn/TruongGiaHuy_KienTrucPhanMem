package model;

import service.Payment;

//concreate-Product
public class VietQr implements Payment {
    @Override
    public void process() {
        System.out.println("Thanh toan VietQR");
    }
}
