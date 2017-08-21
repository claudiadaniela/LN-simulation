package io.gridplus.ln.simulator;

import java.util.*;

import io.gridplus.ln.network.factory.NetworkTopologyAbstractFactory;
import io.gridplus.ln.network.utils.CSVWriter;
import io.gridplus.ln.network.utils.RandomGaussian;
import io.gridplus.ln.scheduler.SchedulerStrategy;
import io.gridplus.ln.scheduler.ShortestQueueStrategy;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.model.NetworkTopology;
import io.gridplus.ln.model.Transfer;
import io.gridplus.ln.view.NetworkGraphView;

public class NetworkSimulatorRunner implements Runnable {
	private List<NetworkClientRunner> clients;
	private SchedulerStrategy strategy;
	private NetworkTopology networkTopo;
	private int maxTransfersPerBlock;
	private int noNodes;
	private int maxHTLC;

	private List<Transfer> generatedTransfers;

	public NetworkSimulatorRunner(NetworkTopology networkTopo, int noHops, int noNodes,
			int noNetworkClientsRunners, int noMaxTransfersPerBlock, int noMaxHTLC) {
		this.networkTopo = networkTopo;
		setupClients(noNetworkClientsRunners);
		this.strategy = new ShortestQueueStrategy();
		this.maxTransfersPerBlock = noMaxTransfersPerBlock;
		this.noNodes = noNodes;
		this.maxHTLC = noMaxHTLC;
		Map<String, Map<String, Integer>> state = networkTopo.getNodesState();
		CSVWriter.writeNetwrokStateData("init-state", state);
		generatedTransfers = generateTransfers();
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
		int block = BlockCounterRunner.getInstance().currentBlock();
		while (BlockCounterRunner.getInstance().running()) {
			int newBlock = BlockCounterRunner.getInstance().currentBlock();
			if (block + 1 == newBlock) {
				strategy.dispatchTransfer(generateTransfers(), clients);
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				block = newBlock;
			}
		}
		System.out.println("--------- FINISHED ---------");
		Map<String, Map<String, Integer>> state = networkTopo.getNodesState();
		CSVWriter.writeNetwrokStateData("final-state", state);
	}

	private List<Transfer> generateTransfers() {
		Set<LNVertex> verticesSet = networkTopo.getVertices();
		LNVertex[] vertices = new LNVertex[verticesSet.size()];
		verticesSet.toArray(vertices);
		List<Transfer> transfers = new ArrayList<Transfer>();
		Random rand = new Random();
		int size = rand.nextInt(maxTransfersPerBlock);
		System.out.println("Generate "+ size+ " for hour " +BlockCounterRunner.getInstance().currentBlock());
		int[] values = RandomGaussian.generate(BlockCounterRunner.getInstance().currentBlock(), size);
		for (int i = 0; i < size; i++) {
			int amount = values[i];
			transfers.add(generateGaussianTransfer(vertices, amount));
		}
		return transfers;
	}

	private Transfer generateRandomTransfer(LNVertex[] vertices) {
		Random rand = new Random();
		LNVertex source = vertices[rand.nextInt(vertices.length)];
		int minAmount = networkTopo.getMinAmountOnNodeEdges(source);
		while (minAmount <= 1) {
			source = vertices[rand.nextInt(vertices.length)];
			minAmount = networkTopo.getMinAmountOnNodeEdges(source);
		}
		LNVertex recipient = vertices[rand.nextInt(vertices.length)];
		while (source.equals(recipient)) {
			recipient = new LNVertex(rand.nextInt(noNodes));
		}
		int amount = rand.nextInt(minAmount/20 - 1) + 1;
		int htlc = rand.nextInt(maxHTLC) + 1;
		Transfer transfer = new Transfer(source, recipient, amount, htlc, rand.nextInt(htlc));
		transfer.setBlockOfDeploymentTime(BlockCounterRunner.getInstance().currentBlock());
		return transfer;
	}
	
	
	private Transfer generateGaussianTransfer(LNVertex[] vertices, int amount) {
		Random rand = new Random();
		LNVertex source = vertices[rand.nextInt(vertices.length)];
		int minAmount = networkTopo.getMinAmountOnNodeEdges(source);
		while (minAmount <= amount) {
			source = vertices[rand.nextInt(vertices.length)];
			minAmount = networkTopo.getMinAmountOnNodeEdges(source);
		}
		LNVertex recipient = vertices[rand.nextInt(vertices.length)];
		while (source.equals(recipient)) {
			recipient = new LNVertex(rand.nextInt(noNodes));
		}
		
		int htlc = rand.nextInt(maxHTLC) + 1;
		Transfer transfer = new Transfer(source, recipient, amount, htlc, rand.nextInt(htlc));
		transfer.setBlockOfDeploymentTime(BlockCounterRunner.getInstance().currentBlock());
		return transfer;
	}

	public static void main(String[] args) {

		int noHops = 2;
		int noClients = 20;
		int noNetworkClientsRunners = 6;
		int noMaxTransfersPerBlock = 100;
		int noMaxHTLC = 2;
		int noSimulationSteps = 24;
		NetworkTopologyAbstractFactory topoFactory = NetworkTopologyAbstractFactory
				.getInstance(NetworkTopologyAbstractFactory.Type.RANDOM);

		NetworkTopology topology = topoFactory.createTopology(noHops, noClients);
		NetworkGraphView graphView = new NetworkGraphView(topology.getNetworkGraph());
		BlockCounterRunner clock = BlockCounterRunner.getInstance();
		clock.setSimulationSteps(noSimulationSteps);
		NetworkSimulatorRunner runner = new NetworkSimulatorRunner(topology, noHops, noClients,
				noNetworkClientsRunners, noMaxTransfersPerBlock, noMaxHTLC);

		new Thread(runner).start();
		new Thread(clock).start();
	}
}
