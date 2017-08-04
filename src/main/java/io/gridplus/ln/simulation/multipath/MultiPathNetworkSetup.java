package io.gridplus.ln.simulation.multipath;

import io.gridplus.ln.simulation.model.LNEdge;
import io.gridplus.ln.simulation.model.LNVertex;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.Set;


public class MultiPathNetworkSetup {
    private SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph;

    public SimpleDirectedWeightedGraph<LNVertex, LNEdge> createNetworkGraph() {
        networkGraph = new SimpleDirectedWeightedGraph<LNVertex, LNEdge>(LNEdge.class);


        LNVertex v0 = new LNVertex(0, 1);
        networkGraph.addVertex(v0);

        LNVertex v1 = new LNVertex(1, 0);
        LNVertex v2 = new LNVertex(2, 0);
        LNVertex v3 = new LNVertex(3, 0);
        LNVertex v4 = new LNVertex(4, 0);
        networkGraph.addVertex(v0);
        networkGraph.addVertex(v1);
        networkGraph.addVertex(v2);
        networkGraph.addVertex(v3);
        networkGraph.addVertex(v4);


        addEdge(v0, v1, LNEdge.ChannelStatus.OPENED, 3, 0);
        addEdge(v0, v2, LNEdge.ChannelStatus.OPENED, 4, 0);
        addEdge(v0, v3, LNEdge.ChannelStatus.OPENED, 5, 0);

        addEdge(v1, v2, LNEdge.ChannelStatus.OPENED, 2, 0);

        addEdge(v2, v3, LNEdge.ChannelStatus.OPENED, 4, 0);
        addEdge(v2, v4, LNEdge.ChannelStatus.OPENED, 1, 0);

        addEdge(v3, v4, LNEdge.ChannelStatus.OPENED, 10, 0);

        return networkGraph;
    }

    private void addEdge(LNVertex v1, LNVertex v2, LNEdge.ChannelStatus status, int tokenAmountV1, int tokenAmountV2) {
        LNEdge e12 = networkGraph.addEdge(v1, v2);
        e12.status = status;
        e12.tokenAmount = tokenAmountV1;
        networkGraph.setEdgeWeight(e12, v1.getFee());

        LNEdge e21 = networkGraph.addEdge(v2, v1);
        e21.status = status;
        e21.tokenAmount = tokenAmountV2;
        networkGraph.setEdgeWeight(e21, v2.getFee());
    }


    public static void main(String[] args) {
        MultiPathNetworkSetup nt = new MultiPathNetworkSetup();
        MinCostMaxFlowAlgorithm flowAlg = new MinCostMaxFlowAlgorithm();
        int noNodes = 5;
        SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph = nt.createNetworkGraph();
        System.out.println(networkGraph.toString());

        int[][] capacity = new int[noNodes][noNodes];
        int[][] fee = new int[noNodes][noNodes];

        for (int i = 0; i < noNodes; i++) {
            for (int j = 0; j < i; j++) {
                Set<LNEdge> edgeSet = networkGraph.getAllEdges(new LNVertex(i), new LNVertex(j));
                for (LNEdge e : edgeSet) {
                    capacity[i][j] = e.tokenAmount;
                    fee[i][j] = (int) e.getWeight();//TODO: Change cast
                }

                Set<LNEdge> edgeSet2 = networkGraph.getAllEdges(new LNVertex(j), new LNVertex(i));
                for (LNEdge e : edgeSet2) {
                    capacity[j][i] = e.tokenAmount;
                    fee[j][i] = (int) e.getWeight();//TODO: Change cast
                }
            }
        }

        int[] ret = flowAlg.getMaxFlow(capacity, fee, 0, 4);
        System.out.println("max flow:" + ret[0] + " cost: " + ret[1]);
    }
}
