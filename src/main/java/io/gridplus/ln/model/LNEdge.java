package io.gridplus.ln.model;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;

public class LNEdge extends DefaultWeightedEdge {

	private static final long serialVersionUID = 1L;
	public ChannelStatus status;
	/**
	 * Token Amount on Directed Edge: A-> B the amount deposited by A
	 * lockedTokenAmount: the amount blocked in transfers from A-> B
	 */
	public int tokenAmount;
	public Map<Integer, Integer> lockedTokenAmount;


	public LNEdge() {
		lockedTokenAmount = new HashMap<Integer, Integer>();
	}

	public int getLockedAmount(int block) {
		Integer amount = lockedTokenAmount.get(block);
		return amount != null ? amount : 0;
	}

	public double getWeight() {
		return super.getWeight();
	}

	// @Override
	// public String toString() {
	// return "" + super.getWeight();
	// }

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
