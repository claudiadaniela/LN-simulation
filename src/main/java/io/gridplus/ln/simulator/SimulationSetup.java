package io.gridplus.ln.simulator;

public enum SimulationSetup {

    SAMPLE_RATE(3000);


    private int value;

    SimulationSetup(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
