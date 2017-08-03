package net.consensys.gridplus.ln.simulation.model;


import org.jgrapht.graph.DefaultWeightedEdge;

public class LNEdge extends DefaultWeightedEdge {
    public ChannelStatus status;
    /**
     * Token Amount on Directed Edge: A-> B the amount deposited by A
     * lockedTokenAmount: the amount blocked in transfers from A-> B
     */
    private double tokenAmount;
    private double lockedTokenAmount;

    @Override
    public String toString() {
            return ""+ super.getWeight();

    }
}
