package io.gridplus.ln.view;

import javax.swing.*;
import java.awt.*;


public class ConfigurationPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JTextField noHopsTxtField;
    private JTextField noNodesTxtField;
    private JTextField initTokenHopTxtField;

    private JTextField noSimulationStepsTxtField;
    private JTextField noNetworkClientsRunnersTxtField;
    private JTextField noMaxTransfersPerBlockTxtField;
    private JTextField noMaxHTLCTxtField;
    private JButton startBtn;

    public ConfigurationPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));

        JPanel nodesConfig = getNodesConfigPanel();
        panel.add(nodesConfig);

        JPanel simConfig = getSimulationConfigPanel();
        panel.add(simConfig);

        JPanel btnPanel = new JPanel();
        startBtn = new JButton("Start Simulation");
        btnPanel.add(startBtn);

        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        this.add(mainPanel);
    }

    private JPanel getNodesConfigPanel() {

        JPanel simulationConfig = new JPanel();
        simulationConfig.setLayout(new GridLayout(3, 3));
        simulationConfig.setBorder(BorderFactory.createTitledBorder(""));

        JLabel noHopsLbl = new JLabel("Hops:");
        noHopsTxtField = new JTextField("2");
        JLabel noNodesLbl = new JLabel("Nodes:");
        noNodesTxtField = new JTextField("20");
        JLabel initTokenHopLbl = new JLabel("Max Init Token:");
        initTokenHopTxtField = new JTextField("100");

        simulationConfig.add(noHopsLbl);
        simulationConfig.add(noHopsTxtField);
        simulationConfig.add(noNodesLbl);
        simulationConfig.add(noNodesTxtField);
        simulationConfig.add(initTokenHopLbl);
        simulationConfig.add(initTokenHopTxtField);

        return simulationConfig;
    }

    private JPanel getSimulationConfigPanel() {

        JPanel simulationConfig = new JPanel();
        simulationConfig.setLayout(new GridLayout(4, 4));
        simulationConfig.setBorder(BorderFactory.createTitledBorder(""));

        JLabel noSimulationStepsLbl = new JLabel("Simulation Steps:");
        noSimulationStepsTxtField = new JTextField("5");

        JLabel noNetworkClientsLbl = new JLabel("Network Clients Th:");
        noNetworkClientsRunnersTxtField = new JTextField("1");

        JLabel noMaxTransfersPerBlockLbl = new JLabel("Max Transfers/uTime:");
        noMaxTransfersPerBlockTxtField = new JTextField("10");

        JLabel noMaxHTLCLbl = new JLabel("Max HTLC:");
        noMaxHTLCTxtField = new JTextField("2");

        simulationConfig.add(noSimulationStepsLbl);
        simulationConfig.add(noSimulationStepsTxtField);
        simulationConfig.add(noNetworkClientsLbl);
        simulationConfig.add(noNetworkClientsRunnersTxtField);
        simulationConfig.add(noMaxTransfersPerBlockLbl);
        simulationConfig.add(noMaxTransfersPerBlockTxtField);
        simulationConfig.add(noMaxHTLCLbl);
        simulationConfig.add(noMaxHTLCTxtField);

        return simulationConfig;
    }

    public String getNoHops() {
        return noHopsTxtField.getText();
    }

    public String getNoNodes() {
        return noNodesTxtField.getText();
    }

    public String getInitTokenHop() {
        return initTokenHopTxtField.getText();
    }

    public String getNoSimulationSteps() {
        return noSimulationStepsTxtField.getText();
    }

    public String getNoNetworkClientsRunners() {
        return noNetworkClientsRunnersTxtField.getText();
    }

    public String getNoMaxTransfersPerBlock() {
        return noMaxTransfersPerBlockTxtField.getText();
    }

    public String getNoMaxHTLC() {
        return noMaxHTLCTxtField.getText();
    }

    public JButton getStartBtn() {
        return startBtn;
    }
}