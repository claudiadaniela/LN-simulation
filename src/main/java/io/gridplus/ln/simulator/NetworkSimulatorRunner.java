package io.gridplus.ln.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import io.gridplus.ln.scheduler.SchedulerStrategy;
import io.gridplus.ln.scheduler.ShortestQueueStrategy;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.model.NetworkTopology;
import io.gridplus.ln.model.Transfer;
import io.gridplus.ln.view.GraphView;

public class NetworkSimulatorRunner implements Runnable {
	private List<NetworkClientRunner> clients;
	private SchedulerStrategy strategy;
	private static NetworkSimulatorRunner instance;
	private NetworkTopology networkTopo;

	private NetworkSimulatorRunner() {
		this.networkTopo = NetworkTopologyGenerator.generateRandomTopology();
		setupClients(SimulationSetup.NO_CLIENT_RUNNERS.value());
		this.strategy = new ShortestQueueStrategy();
	}

	public static NetworkSimulatorRunner getInstance() {
		if (instance == null) {
			synchronized (NetworkSimulatorRunner.class) {
				if (instance == null) {
					instance = new NetworkSimulatorRunner();
				}
			}
		}
		return instance;
	}

	private void setupClients(int size) {
		clients = new ArrayList<NetworkClientRunner>();
		for (int i = 0; i < size; i++) {
			NetworkClientRunner runner = new NetworkClientRunner(i);
			clients.add(runner);
			new Thread(runner).start();
		}
	}

	public void run() {

		while (BlockRunner.getInstance().currentBlock() < SimulationSetup.NO_SIM_STEPS.value()) {
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
		Transfer transfer = new Transfer(source, recipient, amount, htlc, rand.nextInt(htlc));
		transfer.setBlockOfDeploymentTime(BlockRunner.getInstance().currentBlock());
		System.out.println("T: " + transfer);
		return transfer;
	}

	public static void main(String[] args) {
		NetworkSimulatorRunner runner = NetworkSimulatorRunner.getInstance();
		new Thread(runner).start();
		BlockRunner clock = BlockRunner.getInstance();
		new Thread(clock).start();
		GraphView view = new GraphView();
		view.init(runner.networkTopo.getNetworkGraph());
	}
}
