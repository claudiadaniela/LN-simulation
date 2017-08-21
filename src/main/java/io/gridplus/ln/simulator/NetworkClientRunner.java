package io.gridplus.ln.simulator;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import io.gridplus.ln.model.Transfer;
import io.gridplus.ln.model.NetworkTopology;

public class NetworkClientRunner implements Runnable {
	private static final int MAX_ATTEMPTS = 5;
	private int id;
	private NetworkTopology networkTopology;
	private BlockingQueue<Transfer> transfers;

	public NetworkClientRunner(int id, NetworkTopology topology) {
		this.id = id;
		this.networkTopology = topology;
		this.transfers = new LinkedBlockingQueue<>();
	}

	public void addTransfer(Transfer transfer) {
		transfers.add(transfer);
		System.out.println("Transfer added: client " + id + " transfer: " + transfer);
	}

	public void run() {

		while (BlockCounterRunner.getInstance().running()) {
			Transfer transfer;
			try {
				transfer = transfers.peek();
				if (transfer == null
						|| transfer.getBlockOfDeploymentTime() != BlockCounterRunner.getInstance().currentBlock()) {
					continue;
				}
				transfer = transfers.take();
				boolean executed = networkTopology.sendTransfer(transfer);
				int attempt = 1;
				while (!executed && attempt <= MAX_ATTEMPTS) {
					if (!networkTopology.refundHops(transfer)) {
						break;
					}
					executed = networkTopology.sendTransfer(transfer);
					attempt++;
				}
				if (!executed) {
					System.out.println(id + " Drop transfer: " + transfer);
				} else {
					System.out.println(id + " Registered transfer: " + transfer);
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("--------- FINISHED CLIENT "  + id +" ---------");

	}

	public int getSize() {
		return transfers.size();
	}

}
