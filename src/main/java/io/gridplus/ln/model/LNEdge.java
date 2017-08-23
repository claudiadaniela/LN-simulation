package io.gridplus.ln.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.jgrapht.graph.DefaultWeightedEdge;

public class LNEdge extends DefaultWeightedEdge {

	private static final long serialVersionUID = 1L;
	public ChannelStatus status;
	/**
	 * Token Amount on Directed Edge: A-> B the amount deposited by A
	 * lockedTokenAmount: the amount blocked in transfers from A-> B
	 */
	private AtomicInteger tokenAmount;
	public Map<Integer, Integer> lockedTokenAmount;


	public LNEdge() {
		lockedTokenAmount = new HashMap<Integer, Integer>();
		tokenAmount = new AtomicInteger(0);
		this.status = ChannelStatus.OPENED;
	}

	public void addTokenAmount(int amount){
		tokenAmount.addAndGet(amount);
	}
	public int getLockedAmount(int block) {
		Integer amount = lockedTokenAmount.get(block);
		return amount != null ? amount : 0;
	}


	public int getAvailableAmount(int block) {
		return tokenAmount.get() - getLockedAmount(block);
	}
	
	public int getTotalAmount() {
		return tokenAmount.get();
	}

	public double getWeight() {
		return super.getWeight();
	}

	 @Override
	 public String toString() {
	 return "" + super.getSource() +" : "+ tokenAmount.intValue();
	 }

	public LNVertex getSource() {
		return (LNVertex) super.getSource();
	}

	public LNVertex getTarget() {
		return (LNVertex) super.getTarget();
	}

	public enum ChannelStatus {
		OPENED, CLOSED, SETTLED
	}

	public static class LNEdgeComparator implements Comparator<LNEdge> {

		public int compare(LNEdge o1, LNEdge o2) {
			int compVs = o1.getSource().getId() - o2.getSource().getId();
			if (compVs != 0) {
				return (int) compVs;
			}
			return o1.getTarget().getId()- o2.getTarget().getId();
		}

	}


}
