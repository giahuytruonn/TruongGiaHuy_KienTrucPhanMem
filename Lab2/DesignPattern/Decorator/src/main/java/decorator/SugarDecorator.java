package decorator;

import component.Coffee;

public class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public double cost() {
        return coffee.cost() + 0.25;
    }

    @Override
    public String description() {
        return coffee.description() + ", Đường";
    }
}
