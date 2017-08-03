package io.gridplus.ln.simulation.topology;


import io.gridplus.ln.simulation.model.LNEdge;
import io.gridplus.ln.simulation.model.LNVertex;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NetworkTopology {

    private SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph;
    private List<LNVertex> hops;
    private static final double V_TOKEN = 100;

    public SimpleDirectedWeightedGraph<LNVertex, LNEdge> createNetworkGraph(int noHops, int size) {
        networkGraph = new SimpleDirectedWeightedGraph<LNVertex, LNEdge>(LNEdge.class);
        Random rand = new Random();
        hops = new ArrayList<LNVertex>();
        for (int i = 0; i < noHops; i++) {
            double fee = rand.nextDouble();
            LNVertex hop = new LNVertex(i, fee);
            hop.networkStatus = new LNVertex.NetworkStatus(1);
            networkGraph.addVertex(hop);
            hops.add(hop);
            if (i - 1 >= 0) {
                LNVertex hop0 = hops.get(i - 1);
                double tokenAmountV1 = rand.nextInt(100) + V_TOKEN * size;
                double tokenAmountV2 = rand.nextInt(100) + V_TOKEN * size;
                System.out.println("Hop Channel: " + hop0 + "-" + hop);
                addEdge(hop0, hop, LNEdge.ChannelStatus.OPENED, tokenAmountV1, tokenAmountV2);
            }
        }

        for (int i = noHops; i < size + noHops; i++) {
            double fee = rand.nextDouble();
            int selectHopConn = rand.nextInt(noHops);
            LNVertex v1 = new LNVertex(i, fee);
            LNVertex v2 = hops.get(selectHopConn);

            System.out.println("Channel: " + v1 + "-" + v2);
            networkGraph.addVertex(v1);

            double tokenAmountV1 = rand.nextInt(50) + V_TOKEN;
            double tokenAmountV2 = rand.nextInt(50) + V_TOKEN;
            addEdge(v1, v2, LNEdge.ChannelStatus.OPENED, tokenAmountV1, tokenAmountV2);
        }
        return networkGraph;
    }

    private void addEdge(LNVertex v1, LNVertex v2, LNEdge.ChannelStatus status, double tokenAmountV1, double tokenAmountV2) {
        LNEdge e12 = networkGraph.addEdge(v1, v2);
        e12.status = status;
        e12.tokenAmount = tokenAmountV1;
        networkGraph.setEdgeWeight(e12, v1.getFee());
        LNEdge e21 = networkGraph.addEdge(v2, v1);
        e21.status = status;
        e21.tokenAmount = tokenAmountV2;
        networkGraph.setEdgeWeight(e21, v2.getFee());
    }

    public void computeShortestPaths(int id1, int id2) {
        System.out.println("Shortest path from " + id1 + " to " + id2);
        DijkstraShortestPath<LNVertex, LNEdge> dijkstraAlg =
                new DijkstraShortestPath<LNVertex, LNEdge>(networkGraph);
        ShortestPathAlgorithm.SingleSourcePaths<LNVertex, LNEdge> iPaths = dijkstraAlg.getPaths(new LNVertex(id1));
        System.out.println(iPaths.getPath(new LNVertex(id2)) + "\n");
    }
}
