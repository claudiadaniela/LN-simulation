package io.gridplus.ln.view;

import io.gridplus.ln.model.NetworkTopology;
import io.gridplus.ln.simulator.BlockRunner;
import io.gridplus.ln.simulator.NetworkSimulatorRunner;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NetworkSetupController {
    private NetworkSetupView view;

    public NetworkSetupController(NetworkSetupView fame) {
        this.view = fame;
    }

    public void controlSetup() {
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                int noHops = view.getNoHops();
                int noNodes = view.getNoNodes();
                int initTokenHop = view.getInitTokenHop();

                int noSimulationSteps = view.getNoSimulationSteps();
                int noNetworkClientsRunners = view.getNoNetworkClientsRunners();
                int noMaxTransfersPerBlock = view.getNoMaxTransfersPerBlock();
                int noMaxHTLC = view.getNoMaxHTLC();
                setNetworkTopology(noHops, noNodes, initTokenHop, noSimulationSteps, noNetworkClientsRunners, noMaxTransfersPerBlock, noMaxHTLC);
            }
        };
        view.getStartButton().addActionListener(actionListener);
    }

    private void setNetworkTopology(int noHops, int noNodes, int initTokenHop, int noSimulationSteps, int noNetworkClientsRunners, int noMaxTransfersPerBlock, int noMaxHTLC) {
        NetworkSimulatorRunner runner = new NetworkSimulatorRunner(noHops, noNodes, initTokenHop, noNetworkClientsRunners, noMaxTransfersPerBlock, noMaxHTLC);
        BlockRunner clock = BlockRunner.getInstance();
        clock.setSimulationSteps(noSimulationSteps);
        new Thread(runner).start();
        new Thread(clock).start();

       NetworkTopology topology = NetworkTopology.getInstance();
       NetworkGraphView graphView = new NetworkGraphView(topology.getNetworkGraph());

    }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    NetworkSetupView window = new NetworkSetupView();
                    NetworkSetupController controller = new NetworkSetupController(window);
                    controller.controlSetup();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
