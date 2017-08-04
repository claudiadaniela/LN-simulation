package io.gridplus.ln.simulation.network;

import io.gridplus.ln.simulation.model.LNEdge;
import io.gridplus.ln.simulation.model.LNVertex;
import io.gridplus.ln.simulation.multipath.MinCostMaxFlowAlgorithm;
import io.gridplus.ln.simulation.view.GraphView;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;


public class SimStarter {

    public static void main(String[] args) {
        NetworkTopology nt = new NetworkTopology();
        SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph = nt.createNetworkGraph(3, 30);

        System.out.println(networkGraph.toString());
        new GraphView().init(networkGraph);


        nt.computeShortestPath(0, 4);
        nt.computeBFSPaths(0, 4, 2);

    }
}
