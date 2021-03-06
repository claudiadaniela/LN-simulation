package io.gridplus.ln.model;

import org.jgrapht.alg.shortestpath.AbstractPathElement;
import org.jgrapht.alg.shortestpath.PathValidator;

import io.gridplus.ln.simulator.BlockCounterRunner;

public class LNPathValidator implements PathValidator<LNVertex, LNEdge> {
    private double amountNeeded;

    public LNPathValidator(double amount) {
        this.amountNeeded = amount;
    }

    public boolean isValidPath(AbstractPathElement<LNVertex, LNEdge> prevPathElement, LNEdge edge) {
        // TODO: check network status of the nodes
        double availableAmount = edge.getAvailableAmount(BlockCounterRunner.getInstance().currentBlock());
        return availableAmount >= amountNeeded && LNEdge.ChannelStatus.OPENED.equals(edge.status);
    }
}
