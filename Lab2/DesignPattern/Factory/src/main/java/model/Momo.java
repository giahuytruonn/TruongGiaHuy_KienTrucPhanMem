package model;

import service.Payment;

//concreate-Product
public class Momo implements Payment {
    @Override
    public void process() {
        System.out.println("Thanh toan Momo");
    }
}
