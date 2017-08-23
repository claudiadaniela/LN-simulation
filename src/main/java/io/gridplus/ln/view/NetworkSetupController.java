package io.gridplus.ln.view;

import java.awt.EventQueue;
import java.awt.event.ActionListener;

import io.gridplus.ln.model.NetworkTopology;
import io.gridplus.ln.network.factory.NetworkTopologyAbstractFactory;
import io.gridplus.ln.simulator.BlockCounterRunner;
import io.gridplus.ln.simulator.NetworkSimulatorRunner;

public class NetworkSetupController {
    private NetworkSetupView view;
    private NetworkTopologyAbstractFactory.Type type = NetworkTopologyAbstractFactory.Type.RANDOM;

    public NetworkSetupController(NetworkSetupView fame) {
        this.view = fame;
    }

    public void controlSetup() {
        ActionListener actionListener = actionEvent -> {
            int noHops = view.getNoHops();
            int noNodes = view.getNoNodes();
            int initTokenHop = view.getInitTokenHop();

            int noSimulationSteps = view.getNoSimulationSteps();
            int noNetworkClientsRunners = view.getNoNetworkClientsRunners();
            setNetworkTopology(noHops, noNodes, initTokenHop, noSimulationSteps, noNetworkClientsRunners);
        };
        view.getStartButton().addActionListener(actionListener);
    }

    private void setNetworkTopology(int noHops, int noNodes, int initTokenHop, int noSimulationSteps, int noNetworkClientsRunners) {
        NetworkTopologyAbstractFactory topoFactory =NetworkTopologyAbstractFactory.getInstance(type);
        NetworkTopology topology = topoFactory.createTopology(noHops, noNodes);
        NetworkGraphView graphView = new NetworkGraphView(topology.getNetworkGraph());
        BlockCounterRunner clock = BlockCounterRunner.getInstance();
        clock.setSimulationSteps(noSimulationSteps);
       
        NetworkSimulatorRunner runner = new NetworkSimulatorRunner(topology, noHops, noNodes, noNetworkClientsRunners);
        new Thread(runner).start();
        new Thread(clock).start();
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            try {
                NetworkSetupView window = new NetworkSetupView();
                NetworkSetupController controller = new NetworkSetupController(window);
                controller.controlSetup();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

}
