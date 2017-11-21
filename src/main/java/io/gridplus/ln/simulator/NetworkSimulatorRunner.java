package io.gridplus.ln.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.gridplus.ln.generator.factory.TransfersFactory;
import io.gridplus.ln.generator.factory.TransfersFactory.TransfersInput;
import io.gridplus.ln.model.LNEdge;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.model.Transfer;
import io.gridplus.ln.network.topology.NetworkTopology;
import io.gridplus.ln.network.topology.factory.NetworkTopologyAbstractFactory;
import io.gridplus.ln.network.utils.GraphIO;
import io.gridplus.ln.scheduler.SchedulerStrategy;
import io.gridplus.ln.scheduler.ShortestQueueStrategy;
import io.gridplus.ln.simulator.utils.CSVWriter;
import io.gridplus.ln.view.NetworkGraphView;

public class NetworkSimulatorRunner implements Runnable {
    private List<NetworkClientRunner> clients;
    private SchedulerStrategy strategy;
    private NetworkTopology networkTopology;
    private TransfersFactory transfersFactory;
    private static final double CONSUMER_PERCENTAGE = 0.9009;

    public NetworkSimulatorRunner(NetworkTopology networkTopo, int noNodes,
                                  int noNetworkClientsRunners, TransfersInput input) {
        this.networkTopology = networkTopo;
        setupClients(noNetworkClientsRunners);
        setupTransferGenerator(input, noNodes);
        this.strategy = new ShortestQueueStrategy();
        Map<String, Map<String, Double>> state = networkTopo.getNodesState();
        CSVWriter.writeNetwrokStateData("init-state.csv", state);
    }

    private void setupClients(int size) {
        clients = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            NetworkClientRunner runner = new NetworkClientRunner(i, networkTopology);
            clients.add(runner);
            new Thread(runner).start();
        }
    }

    private void setupTransferGenerator(TransfersInput input, int noNodes) {
        Set<LNVertex> verticesSet = networkTopology.getVertices();
        LNVertex[] vertices = new LNVertex[verticesSet.size()];
        verticesSet.toArray(vertices);
        int consumers = (int) (noNodes * CONSUMER_PERCENTAGE);
        transfersFactory = TransfersFactory.getInstance(vertices, input, consumers);
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
        Map<String, Map<String, Double>> state = networkTopology.getNodesState();
        CSVWriter.writeNetwrokStateData("final-state.csv", state);
        CSVWriter.writeHopsRefundsData("hop-refunds.csv", networkTopology.getRefunds());
        CSVWriter.writeHopsFeesData("hop-fees.csv", networkTopology.getFees());
        CSVWriter.writeHopsFeesData("hop-total-flow.csv", networkTopology.getTotalFlow());
        CSVWriter.writeInputEnergyData("inputEnergy.csv", transfersFactory.getEnergyValues());
        CSVWriter.writeConsumptionData("dailyProfileClients.csv", transfersFactory.getClientsDailyProfile());
        CSVWriter.writeConsumptionData("clientsHistogram.csv", transfersFactory.getClientsConsumptionProfileHistogram());
        //NetworkRunnerUtils.updateAndSaveTestNetworkGraph(networkTopology.getNetworkGraph(),  networkTopology.getRefunds());
    }

    public static void main(String[] args) {
        int noHops = 1;
        int noClients = 12;
        int noNetworkClientsRunners = 1;
        int noSimulationSteps = 24;

        NetworkTopologyAbstractFactory topoFactory = NetworkTopologyAbstractFactory
                .getInstance(NetworkTopologyAbstractFactory.Type.RANDOM);

        //NetworkTopology topology = topoFactory.createTopology("./src/main/resources/graph.xml");
        NetworkTopology topology = topoFactory.createTopology(noHops, noClients);
    //    GraphIO.writeGraphML(topology.getNetworkGraph(), "graph.xml");
        NetworkGraphView graphView = new NetworkGraphView(topology.getNetworkGraph());
//        BlockCounterRunner clock = BlockCounterRunner.getInstance();
//        clock.setSimulationSteps(noSimulationSteps);
//        NetworkSimulatorRunner runner = new NetworkSimulatorRunner(topology, noClients,
//                noNetworkClientsRunners, TransfersInput.GAUSSIAN);
//
//        new Thread(runner).start();
//        new Thread(clock).start();



    }
}
