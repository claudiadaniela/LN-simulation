package net.consensys.gridplus.ln.simulation.view;


import javax.swing.*;

import net.consensys.gridplus.ln.simulation.model.LNEdge;
import net.consensys.gridplus.ln.simulation.model.LNVertex;
import org.jgrapht.*;
import org.jgrapht.ext.*;
import com.mxgraph.layout.*;
import com.mxgraph.swing.*;


public class GraphView {


    public void init(Graph<LNVertex, LNEdge> g) {
        JGraphXAdapter<LNVertex, LNEdge> jgxAdapter = new JGraphXAdapter<LNVertex, LNEdge>(g);
        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);
        layout.execute(jgxAdapter.getDefaultParent());

        JFrame frame = new JFrame();
        frame.getContentPane().add(new mxGraphComponent(jgxAdapter));
        frame.setTitle("Network topology");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}