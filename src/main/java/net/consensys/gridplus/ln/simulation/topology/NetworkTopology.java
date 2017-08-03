package net.consensys.gridplus.ln.simulation.topology;


import net.consensys.gridplus.ln.simulation.model.LNEdge;
import net.consensys.gridplus.ln.simulation.model.LNVertex;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NetworkTopology {

    private SimpleDirectedWeightedGraph<LNVertex, LNEdge> netwrokGraph;

    public SimpleDirectedWeightedGraph<LNVertex, LNEdge> createNetwrokGraph(int noHops, int size) {
        netwrokGraph = new SimpleDirectedWeightedGraph<LNVertex, LNEdge>(LNEdge.class);
        Random rand = new Random();
        List<LNVertex> hops = new ArrayList<LNVertex>();
        for (int i = 0; i < noHops; i++) {
            double fee = rand.nextDouble();
            LNVertex hop = new LNVertex(i, fee);
            netwrokGraph.addVertex(hop);
            hops.add(hop);
        }

        for (int i = noHops; i < size+noHops; i++) {
            double fee = rand.nextDouble();
            int selectHopConn = rand.nextInt(noHops);
            LNVertex v1 = new LNVertex(i, fee);
            LNVertex v2 = hops.get(selectHopConn);

            System.out.println("Channel: "+ v1 +"-"+ v2);
            netwrokGraph.addVertex(v1);
            addEdge(v1, v2);
        }
        return netwrokGraph;
    }

    private void addEdge(LNVertex v1, LNVertex v2) {
        LNEdge e12 = netwrokGraph.addEdge(v1, v2);
        netwrokGraph.setEdgeWeight(e12, v1.getFee());
        LNEdge e21 = netwrokGraph.addEdge(v2, v1);
        netwrokGraph.setEdgeWeight(e21, v2.getFee());
    }

    public void computeShortestPaths(int id1, int id2) {
        System.out.println("Shortest path from " + id1 + " to " + id2);
        DijkstraShortestPath<LNVertex, LNEdge> dijkstraAlg =
                new DijkstraShortestPath<LNVertex, LNEdge>(netwrokGraph);
        ShortestPathAlgorithm.SingleSourcePaths<LNVertex, LNEdge> iPaths = dijkstraAlg.getPaths(new LNVertex(id1));
        System.out.println(iPaths.getPath(new LNVertex(id2)) + "\n");
    }
}
