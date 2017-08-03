package net.consensys.gridplus.ln.simulation.starter;

import net.consensys.gridplus.ln.simulation.model.LNEdge;
import net.consensys.gridplus.ln.simulation.model.LNVertex;
import net.consensys.gridplus.ln.simulation.topology.NetworkTopology;
import net.consensys.gridplus.ln.simulation.view.GraphView;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.ListenableDirectedWeightedGraph;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;


public class SimStarter {

    public static void main(String[] args) {
       NetworkTopology nt = new NetworkTopology();
        SimpleDirectedWeightedGraph<LNVertex, LNEdge> stringGraph = nt.createNetwrokGraph(1, 20);

        System.out.println(stringGraph.toString());

        ListenableDirectedWeightedGraph<String, DefaultWeightedEdge> g =
                new ListenableDirectedWeightedGraph<String, DefaultWeightedEdge>(
                        DefaultWeightedEdge.class);
        new GraphView().init(stringGraph);


        nt.computeShortestPaths(2,4);
    }
}
