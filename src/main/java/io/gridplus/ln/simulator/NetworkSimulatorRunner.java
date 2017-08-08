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

public class NetworkSimulatorRunner implements Runnable {
	private List<NetworkClientRunner> clients;
	private SchedulerStrategy strategy;
	private static NetworkSimulatorRunner instance;
	private NetworkTopology networkTopo;
	private int maxTransfersPerBlock;
	private int noNodes;
	private int maxHTLC;

	public NetworkSimulatorRunner( NetworkTopology networkTopo,int noHops, int noNodes, int initTokenHop, int noNetworkClientsRunners, int noMaxTransfersPerBlock, int noMaxHTLC) {
		this.networkTopo = networkTopo;
		setupClients(noNetworkClientsRunners);
		this.strategy = new ShortestQueueStrategy();
		this.maxTransfersPerBlock = noMaxTransfersPerBlock;
		this.noNodes = noNodes;
		this.maxHTLC = noMaxHTLC;
	}


	private void setupClients(int size) {
		clients = new ArrayList<NetworkClientRunner>();
		for (int i = 0; i < size; i++) {
			NetworkClientRunner runner = new NetworkClientRunner(i, networkTopo);
			clients.add(runner);
			new Thread(runner).start();
		}
	}

	public void run() {
		while (BlockRunner.getInstance().running()) {
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
		int size = rand.nextInt(maxTransfersPerBlock);
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
			recipient = new LNVertex(rand.nextInt(noNodes));
		}
		int amount = rand.nextInt(minAmount - 1) + 1;
		int htlc = rand.nextInt(maxHTLC) + 1;
		Transfer transfer = new Transfer(source, recipient, amount, htlc, rand.nextInt(htlc));
		transfer.setBlockOfDeploymentTime(BlockRunner.getInstance().currentBlock());
		System.out.println("T: " + transfer);
		return transfer;
	}

//	public static void main(String[] args) {
//		NetworkSimulatorRunner runner = NetworkSimulatorRunner.getInstance();
//		new Thread(runner).start();
//		BlockRunner clock = BlockRunner.getInstance();
//		new Thread(clock).start();
//		NetworkGraphView view = new NetworkGraphView();
//		view.init(runner.networkTopo.getNetworkGraph());
//	}
}
