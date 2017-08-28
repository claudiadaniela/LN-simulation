package io.gridplus.ln.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.gridplus.ln.generator.factory.TransfersFactory;
import io.gridplus.ln.generator.factory.TransfersFactory.TransfersInput;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.model.NetworkTopology;
import io.gridplus.ln.network.factory.NetworkTopologyAbstractFactory;
import io.gridplus.ln.network.utils.GraphIO;
import io.gridplus.ln.scheduler.SchedulerStrategy;
import io.gridplus.ln.scheduler.ShortestQueueStrategy;
import io.gridplus.ln.simulator.utils.CSVWriter;
import io.gridplus.ln.simulator.utils.NetworkRunnerUtils;

public class NetworkSimulatorRunner implements Runnable {
	private List<NetworkClientRunner> clients;
	private SchedulerStrategy strategy;
	private NetworkTopology networkTopo;
	private TransfersFactory transfersFactory;

	public NetworkSimulatorRunner(NetworkTopology networkTopo, int noHops, int noNodes,
			int noNetworkClientsRunners, TransfersInput input) {
		this.networkTopo = networkTopo;
		setupClients(noNetworkClientsRunners);
		setupTransferGenerator(input);
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

	private void setupTransferGenerator(TransfersInput input){
		Set<LNVertex> verticesSet = networkTopo.getVertices();
		LNVertex[] vertices = new LNVertex[verticesSet.size()];
		verticesSet.toArray(vertices);
		transfersFactory = TransfersFactory.getInstance(vertices, input);
	}

	public void run() {
		int block = -1;
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
		CSVWriter.writeHopsFeesData("hop-total-flow.csv", networkTopo.getTotalFlow());
		CSVWriter.writeInputEnergyData("inputEnergy.csv", transfersFactory.getEnergyValues());
		CSVWriter.writeConsumptionData("dailyProfileClients.csv", transfersFactory.getClientsDailyProfile());
		CSVWriter.writeConsumptionData("clientsHistogram.csv", transfersFactory.getClientsConsumptionProfileHistogram());
		//NetworkRunnerUtils.updateAndSaveTestNetworkGraph(networkTopo.getNetworkGraph(),  networkTopo.getRefunds());
	}

	public static void main(String[] args) {
		int noHops = 1;
		int noClients = 100;
		int noNetworkClientsRunners = 1;
		int noSimulationSteps = 1;

		NetworkTopologyAbstractFactory topoFactory = NetworkTopologyAbstractFactory
				.getInstance(NetworkTopologyAbstractFactory.Type.RANDOM);

		//NetworkTopology topology = topoFactory.createTopology("./src/main/resources/graph.xml");
		NetworkTopology topology = topoFactory.createTopology(1,20);
		GraphIO.writeGraphML(topology.getNetworkGraph(), "graph.xml");
		//NetworkGraphView graphView = new NetworkGraphView(topology.getNetworkGraph());
		BlockCounterRunner clock = BlockCounterRunner.getInstance();
		clock.setSimulationSteps(noSimulationSteps);
		NetworkSimulatorRunner runner = new NetworkSimulatorRunner(topology, noHops, noClients,
				noNetworkClientsRunners, TransfersInput.GAUSSIAN);

		new Thread(runner).start();
		new Thread(clock).start();
	}
}
