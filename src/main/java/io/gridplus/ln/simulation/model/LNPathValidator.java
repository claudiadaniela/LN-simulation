package io.gridplus.ln.simulation.model;

import org.jgrapht.alg.shortestpath.AbstractPathElement;
import org.jgrapht.alg.shortestpath.PathValidator;

import io.gridplus.ln.simulation.model.LNEdge.ChannelStatus;
import io.gridplus.ln.simulation.network.BlockRunner;

public class LNPathValidator implements PathValidator<LNVertex, LNEdge> {
	private int amountNeeded;

	public LNPathValidator(int amount) {
		this.amountNeeded = amount;
	}

	public boolean isValidPath(AbstractPathElement<LNVertex, LNEdge> prevPathElement, LNEdge edge) {
		// TODO: check network status of the nodes
		int lockedAmount = edge.getLockedAmount(BlockRunner.getInstance().currentBlock());
		return edge.tokenAmount - lockedAmount >= amountNeeded && ChannelStatus.OPENED.equals(edge.status);
	}
}
