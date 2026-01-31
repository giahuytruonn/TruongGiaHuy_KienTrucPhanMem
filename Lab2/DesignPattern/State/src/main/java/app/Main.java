package app;

import context.Light;

public class Main {
    public static void main(String[] args) {
        Light light = new Light();

        light.turnOn();
        light.turnOn();

        light.turnOff();
        light.turnOff();

        light.turnOn();
    }
}
