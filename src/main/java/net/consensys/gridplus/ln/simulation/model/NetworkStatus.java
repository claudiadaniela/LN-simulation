package net.consensys.gridplus.ln.simulation.model;


import java.io.Serializable;

public class NetworkStatus implements Serializable {
    private double healthScore;

    public NetworkStatus(double healthScore) {
        this.healthScore = healthScore;
    }

    public double getHealthScore() {
        return healthScore;
    }
}