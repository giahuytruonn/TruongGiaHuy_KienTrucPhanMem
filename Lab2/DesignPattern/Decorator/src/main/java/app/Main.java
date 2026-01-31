package app;

import component.Coffee;
import component.SimpleCoffee;
import decorator.MilkDecorator;
import decorator.SugarDecorator;

public class Main {
    public static void main(String[] args) {
        Coffee coffee = new SimpleCoffee();
        System.out.println(coffee.description() + " - $" + coffee.cost());

        coffee = new MilkDecorator(coffee);
        System.out.println(coffee.description() + " - $" + coffee.cost());

        coffee = new SugarDecorator(coffee);
        System.out.println(coffee.description() + " - $" + coffee.cost());

        Coffee coffee2 = new SugarDecorator(new MilkDecorator(new SimpleCoffee()));
        System.out.println(coffee2.description() + " - $" + coffee2.cost());
    }
}
