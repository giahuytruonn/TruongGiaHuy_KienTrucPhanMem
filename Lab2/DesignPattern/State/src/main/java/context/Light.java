package context;

import state.State;
import state.OnState;
import state.OffState;

public class Light {
    private State state;

    public Light() {
        this.state = new OffState();
    }

    public void setState(State state) {
        this.state = state;
    }

    public void turnOn() {
        state.turnOn();
        if (!(state instanceof OnState)) {
            this.state = new OnState();
        }
    }

    public void turnOff() {
        state.turnOff();
        if (!(state instanceof OffState)) {
            this.state = new OffState();
        }
    }
}
