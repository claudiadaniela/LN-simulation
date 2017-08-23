package io.gridplus.ln.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.gridplus.ln.generator.factory.TransfersFactory;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.model.NetworkTopology;
import io.gridplus.ln.network.factory.NetworkTopologyAbstractFactory;
import io.gridplus.ln.network.utils.CSVWriter;
import io.gridplus.ln.scheduler.SchedulerStrategy;
import io.gridplus.ln.scheduler.ShortestQueueStrategy;
import io.gridplus.ln.view.NetworkGraphView;

public class NetworkSimulatorRunner implements Runnable {
	private List<NetworkClientRunner> clients;
	private SchedulerStrategy strategy;
	private NetworkTopology networkTopo;
	private TransfersFactory transfersFactory;

	public NetworkSimulatorRunner(NetworkTopology networkTopo, int noHops, int noNodes,
			int noNetworkClientsRunners,  int noMaxHTLC) {
		this.networkTopo = networkTopo;
		setupClients(noNetworkClientsRunners);
		setupTransferGenerator(noMaxHTLC);
		this.strategy = new ShortestQueueStrategy();
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
				strategy.dispatchTransfer(transfersFactory.generate(newBlock), clients);
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
		CSVWriter.writeHopsRefundsData("hop-refunds.csv", networkTopo.getRefunds());
		CSVWriter.writeHopsFeesData("hop-fees.csv", networkTopo.getFees());
		CSVWriter.writeInputEnergyData("inptEnergy.csv", transfersFactory.getEnergyValues());
	}

	public static void main(String[] args) {
		int noHops = 1;
		int noClients = 100;
		int noNetworkClientsRunners = 1;
		int noMaxHTLC = 2;
		int noSimulationSteps = 24;

		NetworkTopologyAbstractFactory topoFactory = NetworkTopologyAbstractFactory
				.getInstance(NetworkTopologyAbstractFactory.Type.RANDOM);

		NetworkTopology topology = topoFactory.createTopology(noHops, noClients);
		NetworkGraphView graphView = new NetworkGraphView(topology.getNetworkGraph());
		BlockCounterRunner clock = BlockCounterRunner.getInstance();
		clock.setSimulationSteps(noSimulationSteps);
		NetworkSimulatorRunner runner = new NetworkSimulatorRunner(topology, noHops, noClients,
				noNetworkClientsRunners, noMaxHTLC);

		new Thread(runner).start();
		new Thread(clock).start();
	}
}
