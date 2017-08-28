package io.gridplus.ln.simulator;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.gridplus.ln.model.NetworkTopology;
import io.gridplus.ln.model.Transfer;

public class NetworkClientRunner implements Runnable {
	private static final int MAX_ATTEMPTS = 5;
	private int id;
	private NetworkTopology networkTopology;
	private BlockingQueue<Transfer> transfers;
	private static final Logger LOGGER = Logger.getLogger(NetworkClientRunner.class
			.getName());
	public NetworkClientRunner(int id, NetworkTopology topology) {
		this.id = id;
		this.networkTopology = topology;
		this.transfers = new LinkedBlockingQueue<>();
	}

	public void addTransfer(Transfer transfer) {
		transfers.add(transfer);
	}

	public void run() {
		LOGGER.log(Level.INFO, "--------- STARTED CLIENT "  + id +" ---------");
		while (BlockCounterRunner.getInstance().running() || transfers.size()>0) {
			Transfer transfer;
			try {
				transfer = transfers.peek();
				if (transfer == null
						|| transfer.getBlockOfDeploymentTime() > BlockCounterRunner.getInstance().currentBlock()) {
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
				if (!executed) {LOGGER.log(Level.WARNING, id + " Drop transfer: " + transfer);}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		LOGGER.log(Level.INFO, "--------- FINISHED CLIENT "  + id +" ---------");
	}

	public boolean running(){
		return BlockCounterRunner.getInstance().running() || transfers.size()>0;
	}

	public int getSize() {
		return transfers.size();
	}

}
