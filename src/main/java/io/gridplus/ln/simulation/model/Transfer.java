package io.gridplus.ln.simulation.model;

import java.util.Comparator;
import java.util.Random;

public class Transfer {
	private LNVertex source;
	private LNVertex recipient;
	private int amount;
	/**
	 * Simulate the time that a transfer locks the tokens lockTime< HTLC-time
	 */
	private int lockTime;
	private int htlcTime;
	/**
	 * Field used for scheduling transfers
	 */
	private int blockOfDeploymentTime;

	public Transfer(LNVertex source, LNVertex recipient, int amount, int htlc) {
		super();
		this.source = source;
		this.recipient = recipient;
		this.amount = amount;
		this.htlcTime = htlc;
		Random rand = new Random();
		this.lockTime = rand.nextInt(htlc);
	}

	public LNVertex getSource() {
		return source;
	}

	public LNVertex getRecipient() {
		return recipient;
	}

	public int getAmount() {
		return amount;
	}

	public int getLockTime() {
		return lockTime;
	}

	public int getHtlcTime() {
		return htlcTime;
	}

	public int getBlockOfDeploymentTime() {
		return blockOfDeploymentTime;
	}

	public void setBlockOfDeploymentTime(int blockOfDeploymentTime) {
		this.blockOfDeploymentTime = blockOfDeploymentTime;
	}

	@Override
	public String toString() {
		return "Transfer [source=" + source + ", recipient=" + recipient + ", amount=" + amount +", deploy time=" + blockOfDeploymentTime + "]";
	}

	public static class TransferComparator implements Comparator<Transfer> {
		public int compare(Transfer o1, Transfer o2) {
			return o1.blockOfDeploymentTime - o2.blockOfDeploymentTime;
		}

	}
}
