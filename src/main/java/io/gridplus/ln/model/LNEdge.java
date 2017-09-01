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
	private volatile double tokenAmount;
	public Map<Integer, Double> lockedTokenAmount;

	public LNEdge() {
		lockedTokenAmount = new HashMap<>();
		tokenAmount = 0;
		this.status = ChannelStatus.OPENED;
	}

	public synchronized void addTokenAmount(double amount){
		tokenAmount+=amount;
	}
	public synchronized void setTokenAmount(double amount){
		tokenAmount= amount;
	}
	public double getLockedAmount(int block) {
		Double amount = lockedTokenAmount.get(block);
		return amount != null ? amount : 0;
	}


	public double getAvailableAmount(int block) {
		return tokenAmount - getLockedAmount(block);
	}
	
	public double getTotalAmount() {
		return tokenAmount;
	}

	public double getWeight() {
		return super.getWeight();
	}

	 @Override
	 public String toString() {
	 return "" ;//+ super.getSource() +" : "+ tokenAmount.intValue();
	 }

	public LNVertex getSource() {
		return (LNVertex) super.getSource();
	}

	public LNVertex getTarget() {
		return (LNVertex) super.getTarget();
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LNEdge lnEdge = (LNEdge) o;

		if (!super.getSource().equals(lnEdge.getSource())) return false;
		if (!super.getTarget().equals(lnEdge.getTarget())) return false;
		return true;
	}


	@Override
	public int hashCode() {
		int r2=(super.getSource()!=null)? super.getSource().hashCode():0;
		int r3=  (super.getTarget()!=null)? super.getTarget().hashCode():0;
		int result = 31 *  r2;
		result = 31 * result + r3;
		return result;
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
