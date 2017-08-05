package io.gridplus.ln.simulation.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import io.gridplus.ln.simulation.model.LNVertex;
import io.gridplus.ln.simulation.model.NetworkTopology;
import io.gridplus.ln.simulation.model.Transfer;
import io.gridplus.ln.simulation.scheduler.SchedulerStrategy;
import io.gridplus.ln.simulation.scheduler.ShortestQueueStrategy;

public class NetworkRunner implements Runnable {
	private List<ClientRunner> clients;
	private SchedulerStrategy strategy;
	private static NetworkRunner instance;
	private NetworkTopology networkTopo;

	private NetworkRunner() {
		this.networkTopo = NetworkTopologyGenerator.generateRandomTopology();
		setupClients(SimulationSetup.NO_CLIENT_RUNNERS.value());
		this.strategy = new ShortestQueueStrategy();
	}

	public static NetworkRunner getInstance() {
		if (instance == null) {
			synchronized (NetworkRunner.class) {
				if (instance == null) {
					instance = new NetworkRunner();
				}
			}
		}
		return instance;
	}

	private void setupClients(int size) {
		clients = new ArrayList<ClientRunner>();
		for (int i = 0; i < size; i++) {
			ClientRunner runner = new ClientRunner(i);
			clients.add(runner);
			new Thread(runner).start();
		}
	}

	public void run() {

		while (BlockClock.getInstance().currentBlock() < SimulationSetup.NO_SIM_STEPS.value()) {
			strategy.dispatchTransfer(generateTransfers(), clients);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private List<Transfer> generateTransfers() {
		Set<LNVertex> verticesSet = networkTopo.getVertices();
		LNVertex[] vertices = new LNVertex[verticesSet.size()];
		verticesSet.toArray(vertices);
		List<Transfer> transfers = new ArrayList<Transfer>();
		Random rand = new Random();
		int size = rand.nextInt(SimulationSetup.MAX_TRANSFERS_PER_BLOCK.value());
		for (int i = 0; i < size; i++) {
			transfers.add(generateRandomTransfer(vertices));
		}
		return transfers;
	}

	private Transfer generateRandomTransfer(LNVertex[] vertices) {
		Random rand = new Random();
		LNVertex source = vertices[rand.nextInt(vertices.length)];
		int minAmount = networkTopo.getMinAmountOnNodeEdges(source);
		while(minAmount<=1){
			source = vertices[rand.nextInt(vertices.length)];
			minAmount = networkTopo.getMinAmountOnNodeEdges(source);
		}
		LNVertex recipient = vertices[rand.nextInt(vertices.length)];
		while (source.equals(recipient)) {
			recipient = new LNVertex(rand.nextInt(SimulationSetup.NO_NODES.value()));
		}
		int amount = rand.nextInt(minAmount - 1) + 1;
		int htlc = rand.nextInt(SimulationSetup.MAX_HTLC.value()) + 1;
		Transfer transfer = new Transfer(source, recipient, amount, htlc);
		transfer.setBlockOfDeploymentTime(BlockClock.getInstance().currentBlock());
		System.out.println("T: " + transfer);
		return transfer;
	}

	public static void main(String[] args) {
		NetworkRunner runner = NetworkRunner.getInstance();
		new Thread(runner).start();
		BlockClock clock = BlockClock.getInstance();
		new Thread(clock).start();
	}
}
