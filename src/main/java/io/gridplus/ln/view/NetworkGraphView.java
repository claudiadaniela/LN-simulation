package io.gridplus.ln.view;

import javax.swing.JFrame;

import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.swing.mxGraphComponent;

import io.gridplus.ln.model.LNEdge;
import io.gridplus.ln.model.LNVertex;

public class NetworkGraphView {

	public NetworkGraphView(SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph) {
		JGraphXAdapter<LNVertex, LNEdge> jgxAdapter = new JGraphXAdapter<LNVertex, LNEdge>(networkGraph);
		mxFastOrganicLayout layout = new mxFastOrganicLayout(jgxAdapter);
		layout.execute(jgxAdapter.getDefaultParent());

		JFrame frame = new JFrame();
		frame.getContentPane().add(new mxGraphComponent(jgxAdapter));
		frame.setTitle("Network topology");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}


}