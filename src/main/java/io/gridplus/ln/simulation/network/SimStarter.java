package io.gridplus.ln.simulation.network;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import io.gridplus.ln.simulation.model.LNEdge;
import io.gridplus.ln.simulation.model.LNVertex;
import io.gridplus.ln.simulation.model.NetworkTopology;
import io.gridplus.ln.simulation.view.GraphView;

public class SimStarter {

	public static void main(String[] args) {
		NetworkTopology nt = NetworkTopologyGenerator.generateRandomTopology(3, 30);

		SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph = nt.getNetworkGraph();
		System.out.println(networkGraph.toString());
		new GraphView().init(networkGraph);

		nt.computeShortestPaths(0, 4, 2);

	}
}
