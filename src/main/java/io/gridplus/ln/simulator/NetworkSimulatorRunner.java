package io.gridplus.ln.simulator;

import java.util.*;

import io.gridplus.ln.generator.factory.TransfersFactory;
import io.gridplus.ln.network.factory.NetworkTopologyAbstractFactory;
import io.gridplus.ln.network.utils.CSVWriter;
import io.gridplus.ln.scheduler.SchedulerStrategy;
import io.gridplus.ln.scheduler.ShortestQueueStrategy;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.model.NetworkTopology;
import io.gridplus.ln.view.NetworkGraphView;

public class NetworkSimulatorRunner implements Runnable {
	private List<NetworkClientRunner> clients;
	private SchedulerStrategy strategy;
	private NetworkTopology networkTopo;
	private int maxTransfersPerBlock;
	private int noNodes;
	private int maxHTLC;
	private TransfersFactory transfersFactory;
	public NetworkSimulatorRunner(NetworkTopology networkTopo, int noHops, int noNodes,
			int noNetworkClientsRunners, int noMaxTransfersPerBlock, int noMaxHTLC) {
		this.networkTopo = networkTopo;
		setupClients(noNetworkClientsRunners);
		setupTransferGenerator(noMaxHTLC);
		this.strategy = new ShortestQueueStrategy();
		this.maxTransfersPerBlock = noMaxTransfersPerBlock;
		this.noNodes = noNodes;
		this.maxHTLC = noMaxHTLC;
		Map<String, Map<String, Integer>> state = networkTopo.getNodesState();
		CSVWriter.writeNetwrokStateData("init-state.csv", state);


	}

	private void setupClients(int size) {
		clients = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			NetworkClientRunner runner = new NetworkClientRunner(i, networkTopo);
			clients.add(runner);
			new Thread(runner).start();
		}
	}

	private void setupTransferGenerator(int maxHTLC){
		Set<LNVertex> verticesSet = networkTopo.getVertices();
		LNVertex[] vertices = new LNVertex[verticesSet.size()];
		verticesSet.toArray(vertices);
		transfersFactory = new TransfersFactory(vertices,maxHTLC );
	}
	public void run() {
		int block = BlockCounterRunner.getInstance().currentBlock();
		while (BlockCounterRunner.getInstance().running()) {
			int newBlock = BlockCounterRunner.getInstance().currentBlock();
			if (block + 1 == newBlock) {
				strategy.dispatchTransfer(transfersFactory.generate(newBlock,noNodes), clients);
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
		CSVWriter.writeNetwrokStateData("final-state.csv", state);
	}

	public static void main(String[] args) {

		int noHops = 2;
		int noClients = 20;
		int noNetworkClientsRunners = 1;
		int noMaxTransfersPerBlock = 50;
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
