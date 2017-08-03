package net.consensys.gridplus.ln.simulation.model;


import java.io.Serializable;

public class LNVertex implements Serializable{
    private int id;
    private double fee;

    private NetworkStatus networkStatus;

    public LNVertex(int id) {
        this.id = id;
    }

    public LNVertex(int id, double fee) {
        this.id = id;
        this.fee = fee;
    }

    public int getId() {
        return id;
    }

    public double getFee() {
        return fee;
    }

    public NetworkStatus getNetworkStatus() {
        return networkStatus;
    }

    public void setNetworkStatus(NetworkStatus networkStatus) {
        this.networkStatus = networkStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LNVertex lnVertex = (LNVertex) o;

        return id == lnVertex.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "V"+id;
    }
}
