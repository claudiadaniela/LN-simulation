package io.gridplus.ln.simulation.model;

import org.jgrapht.graph.DefaultWeightedEdge;

public class LNEdge extends DefaultWeightedEdge {

	private static final long serialVersionUID = 1L;
	public ChannelStatus status;
	/**
	 * Token Amount on Directed Edge: A-> B the amount deposited by A
	 * lockedTokenAmount: the amount blocked in transfers from A-> B
	 */
	public int tokenAmount;
	public int lockedTokenAmount;
	public int fee;

	public double getWeight() {
		return super.getWeight();
	}

//	@Override
//	public String toString() {
//		return "" + super.getWeight();
//	}

	public LNVertex getSource() {
		return (LNVertex) super.getSource();
	}

	public LNVertex getTarget() {
		return (LNVertex) super.getTarget();
	}

	public enum ChannelStatus {
		OPENED, CLOSED, SETTLED
	}
}
