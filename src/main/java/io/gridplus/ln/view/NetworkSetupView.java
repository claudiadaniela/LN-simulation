package io.gridplus.ln.view;


import javax.swing.*;
import java.awt.*;

public class NetworkSetupView {

    private JFrame frame;
    private ConfigurationPanel configPanel;

    public NetworkSetupView() {
        initialize();
    }


    private void initialize() {
        frame = new JFrame();
        frame.setBounds(300, 300, 600, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPaneDemo tabbedPane = new JTabbedPaneDemo();
        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }


    private class JTabbedPaneDemo extends JPanel {
        private static final long serialVersionUID = 1L;

        public JTabbedPaneDemo() {
            JTabbedPane jtbExample = new JTabbedPane();
            JPanel jplInnerPanel = createConfigPanel();
            jtbExample.addTab("Network Configuration", null, jplInnerPanel, "Tab");
            setLayout(new GridLayout(1, 1));
            add(jtbExample);
        }

        private JPanel createConfigPanel() {
            configPanel = new ConfigurationPanel();
            return configPanel;
        }
    }

    public JButton getStartButton(){
        return configPanel.getStartBtn();
    }

    public int getNoHops() {
        return Integer.parseInt(configPanel.getNoHops());
    }

    public int getNoNodes() {
        return Integer.parseInt(configPanel.getNoNodes());
    }

    public int getInitTokenHop() {
        return Integer.parseInt(configPanel.getInitTokenHop());
    }

    public int getNoSimulationSteps() {
        return Integer.parseInt(configPanel.getNoSimulationSteps());
    }

    public int getNoNetworkClientsRunners() {
        return Integer.parseInt(configPanel.getNoNetworkClientsRunners());
    }

    public int getNoMaxTransfersPerBlock() {
        return Integer.parseInt(configPanel.getNoMaxTransfersPerBlock());
    }

    public int getNoMaxHTLC() {
        return Integer.parseInt(configPanel.getNoMaxHTLC());
    }
}
