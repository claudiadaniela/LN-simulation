package io.gridplus.ln.simulation.network;

import io.gridplus.ln.simulation.model.LNEdge;
import io.gridplus.ln.simulation.model.LNVertex;
import org.jgrapht.alg.shortestpath.AbstractPathElement;
import org.jgrapht.alg.shortestpath.PathValidator;


public class LNPathValidator implements PathValidator<LNVertex, LNEdge> {
    private int amountNeeded;

    public LNPathValidator(int amount) {
        this.amountNeeded = amount;
    }

    public boolean isValidPath(AbstractPathElement<LNVertex, LNEdge> prevPathElement, LNEdge edge) {
        return edge.tokenAmount-edge.lockedTokenAmount >= amountNeeded;
    }
}