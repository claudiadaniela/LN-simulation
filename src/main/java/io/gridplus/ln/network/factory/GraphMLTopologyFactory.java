package io.gridplus.ln.network.factory;

import io.gridplus.ln.model.LNEdge;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.model.NetworkTopology;
import io.gridplus.ln.network.utils.GraphIO;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;


public class GraphMLTopologyFactory extends NetworkTopologyAbstractFactory {
   // private static final String FILE = "./src/main/resources/graph.xml";

    @Override
    public NetworkTopology createTopology(int noHops, int noNodes) {
        return  new NetworkTopology();
    }

    @Override
    public NetworkTopology createTopology(String file) {
        SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph = GraphIO.readGraphML(file);
        return new NetworkTopology(networkGraph);

    }
}
