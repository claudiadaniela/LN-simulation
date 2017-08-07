package io.gridplus.ln.view;

import io.gridplus.ln.simulator.BlockRunner;
import io.gridplus.ln.simulator.NetworkSimulatorRunner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NetwrokSetupController {
    private NetworkSetupView view;

    public NetwrokSetupController(NetworkSetupView fame) {
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
        new Thread(runner).start();
        BlockRunner clock = BlockRunner.getInstance();
        clock.setSimulationSteps(noSimulationSteps);
        new Thread(clock).start();
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    NetworkSetupView window = new NetworkSetupView();
                    NetwrokSetupController controller = new NetwrokSetupController(window);
                    controller.controlSetup();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
