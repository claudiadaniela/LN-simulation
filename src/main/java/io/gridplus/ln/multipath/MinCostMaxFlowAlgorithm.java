package io.gridplus.ln.multipath;

import io.gridplus.ln.model.LNEdge;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.network.topology.NetworkTopology;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Set;

/**
 * SOURCE :
 * https://github.com/jaehyunp/stanfordacm/blob/master/code/MinCostMaxFlow.java
 */
public class MinCostMaxFlowAlgorithm {
    private boolean found[];
    private int noNodes, flow[][], dad[];
    private double capacity[][];
    private double dist[], pi[];
    private double cost[][];

    static final int INF = Integer.MAX_VALUE / 2 - 1;

    private boolean search(int source, int sink) {
        Arrays.fill(found, false);
        Arrays.fill(dist, INF);
        dist[source] = 0;

        while (source != noNodes) {
            int best = noNodes;
            found[source] = true;
            for (int k = 0; k < noNodes; k++) {
                if (found[k])
                    continue;
                if (flow[k][source] != 0) {
                    double val = dist[source] + pi[source] - pi[k] - cost[k][source];
                    if (dist[k] > val) {
                        dist[k] = val;
                        dad[k] = source;
                    }
                }
                if (flow[source][k] < capacity[source][k]) {
                    double val = dist[source] + pi[source] - pi[k] + cost[source][k];
                    if (dist[k] > val) {
                        dist[k] = val;
                        dad[k] = source;
                    }
                }

                if (dist[k] < dist[best])
                    best = k;
            }
            source = best;
        }
        for (int k = 0; k < noNodes; k++)
            pi[k] = Math.min(pi[k] + dist[k], INF);
        return found[sink];
    }

    public double[] getMaxFlow(int source, int sink) {


        int totflow = 0;
        double totcost = 0;
        while (search(source, sink)) {
            double amt = INF;
            for (int x = sink; x != source; x = dad[x])
                amt = Math.min(amt, flow[x][dad[x]] != 0 ? flow[x][dad[x]] : capacity[dad[x]][x] - flow[dad[x]][x]);
            for (int x = sink; x != source; x = dad[x]) {
                if (flow[x][dad[x]] != 0) {
                    flow[x][dad[x]] -= amt;
                    totcost -= amt * cost[x][dad[x]];
                } else {
                    flow[dad[x]][x] += amt;
                    totcost += amt * cost[dad[x]][x];
                }
            }
            totflow += amt;
        }

        return new double[]{totflow, totcost};
    }

    public static MinCostMaxFlowAlgorithm getInstance(SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph) {
        MinCostMaxFlowAlgorithm alg = new MinCostMaxFlowAlgorithm();
        alg.noNodes = networkGraph.vertexSet().size();
        double[][] capacity = new double[alg.noNodes][alg.noNodes];
        double[][] fee = new double[alg.noNodes][alg.noNodes];

        for (int i = 0; i < alg.noNodes; i++) {
            for (int j = 0; j < i; j++) {
                Set<LNEdge> edgeSet = networkGraph.getAllEdges(new LNVertex(i), new LNVertex(j));
                for (LNEdge e : edgeSet) {
                    capacity[i][j] = e.getTotalAmount();
                    fee[i][j] = e.getWeight();
                }

                Set<LNEdge> edgeSet2 = networkGraph.getAllEdges(new LNVertex(j), new LNVertex(i));
                for (LNEdge e : edgeSet2) {
                    capacity[j][i] = e.getTotalAmount();
                    fee[j][i] = e.getWeight();
                }
            }
        }

        alg.capacity = capacity;
        alg.cost = fee;

        alg.found = new boolean[alg.noNodes];
        alg.flow = new int[alg.noNodes][alg.noNodes];
        alg.dist = new double[alg.noNodes + 1];
        alg.dad = new int[alg.noNodes];
        alg.pi = new double[alg.noNodes];
        return alg;
    }
    
    
    public static void main(String[] args){
    	NetworkTopology networkTop = new NetworkTopology();
        LNVertex v0 = networkTop.addNode(0, 0, new LNVertex.NetworkStatus(1), false);
        LNVertex v1 = networkTop.addNode(1, 0, new LNVertex.NetworkStatus(1), false);
        LNVertex v2 = networkTop.addNode(2, 0, new LNVertex.NetworkStatus(1), false);
        LNVertex v3 = networkTop.addNode(3, 1, new LNVertex.NetworkStatus(1), false);
        LNVertex v4 = networkTop.addNode(4, 0, new LNVertex.NetworkStatus(1), false);

        networkTop.addChannel(v0, v1, LNEdge.ChannelStatus.OPENED, 3, 0);
        networkTop.addChannel(v0, v2, LNEdge.ChannelStatus.OPENED, 4, 0);
        networkTop.addChannel(v0, v3, LNEdge.ChannelStatus.OPENED, 5, 0);

        networkTop.addChannel(v1, v2, LNEdge.ChannelStatus.OPENED, 2, 0);

        networkTop.addChannel(v2, v3, LNEdge.ChannelStatus.OPENED, 4, 0);
        networkTop.addChannel(v2, v4, LNEdge.ChannelStatus.OPENED, 1, 0);

        networkTop.addChannel(v3, v4, LNEdge.ChannelStatus.OPENED, 10, 0);
        MinCostMaxFlowAlgorithm flowAlg = MinCostMaxFlowAlgorithm.getInstance(networkTop.getNetworkGraph());
        
        double[] ret = flowAlg.getMaxFlow(0, 4);
        System.out.println("Max flow "+ ret[0]);
        System.out.println("Min cost "+ ret[1]);
    }
}