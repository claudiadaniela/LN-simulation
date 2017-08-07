package io.gridplus.ln.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import io.gridplus.ln.simulator.BlockRunner;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.KShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.alg.shortestpath.PathValidator;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import io.gridplus.ln.model.LNVertex.NetworkStatus;

public class NetworkTopology {

    private SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph;
    private List<LNVertex> hops;
    private static NetworkTopology instance;

    private NetworkTopology() {
        this.networkGraph = new SimpleDirectedWeightedGraph<LNVertex, LNEdge>(LNEdge.class);
    }

    public static NetworkTopology getInstance() {
        if (instance == null) {
            synchronized (NetworkTopology.class) {
                if (instance == null) {
                    instance = new NetworkTopology();
                }
            }
        }
        return instance;
    }

    public void setHops(List<LNVertex> list) {
        this.hops = list;
    }

    public LNVertex addNode(int id, double fee, NetworkStatus status) {
        LNVertex vertex = new LNVertex(id, fee);
        vertex.networkStatus = new NetworkStatus(1);
        networkGraph.addVertex(vertex);
        return vertex;
    }

    public void addChannel(LNVertex v1, LNVertex v2, LNEdge.ChannelStatus status, int tokenAmountV1,
                           int tokenAmountV2) {
        LNEdge e12 = networkGraph.addEdge(v1, v2);
        e12.status = status;
        e12.tokenAmount = tokenAmountV1;
        networkGraph.setEdgeWeight(e12, v1.getFee());
        LNEdge e21 = networkGraph.addEdge(v2, v1);
        e21.status = status;
        e21.tokenAmount = tokenAmountV2;
        networkGraph.setEdgeWeight(e21, v2.getFee());
    }

    public List<GraphPath<LNVertex, LNEdge>> findShortestPaths(LNVertex id1, LNVertex id2, int amount,
                                                               PathValidator<LNVertex, LNEdge> validator) {
        KShortestPathAlgorithm<LNVertex, LNEdge> pathsAlg = new KShortestPaths<LNVertex, LNEdge>(networkGraph, 10,
                validator);
        List<GraphPath<LNVertex, LNEdge>> paths = pathsAlg.getPaths(id1, id2);
        Collections.sort(paths, new LNPathComparator());
        return paths;
    }

    public LNEdge getEdge(LNVertex v1, LNVertex v2) {
        return networkGraph.getEdge(v1, v2);
    }

    public Set<LNEdge> getEdges(LNVertex v1) {
        return networkGraph.edgesOf(v1);
    }

    public int getMinAmountOnNodeEdges(LNVertex v1) {
        int currentBlock = BlockRunner.getInstance().currentBlock();
        Set<LNEdge> edges = getEdges(v1);

        int min = Integer.MAX_VALUE;
        for (LNEdge e : edges) {
            int availableAmount = e.tokenAmount - e.getLockedAmount(currentBlock);
            if (availableAmount < min) {
                min = availableAmount;
            }
        }
        return min != Integer.MAX_VALUE ? min : 0;
    }

    public Set<LNVertex> getVertices() {
        return networkGraph.vertexSet();
    }

    public SimpleDirectedWeightedGraph<LNVertex, LNEdge> getNetworkGraph() {
        return networkGraph;
    }

    public boolean sendTransfer(Transfer transfer) {
        List<GraphPath<LNVertex, LNEdge>> paths = findShortestPaths(transfer.getSource(), transfer.getRecipient(),
                transfer.getAmount(), new LNPathValidator(transfer.getAmount()));
        if (paths == null || paths.size() == 0) {
            return false;
        }
        GraphPath<LNVertex, LNEdge> bestPath = paths.get(0);
        List<LNEdge> edges = bestPath.getEdgeList();
        int lockedTime = transfer.getLockTime();
        int currentBlock = BlockRunner.getInstance().currentBlock();
        int amount = transfer.getAmount();
        for (LNEdge exy : edges) {
            LNVertex ex = exy.getSource();
            LNVertex ey = exy.getTarget();
            LNEdge eyx = getEdge(ey, ex);
            if (eyx != null) {
                exy.tokenAmount -= amount;
                eyx.tokenAmount += amount;
                for (int i = currentBlock; i < lockedTime; i++) {
                    eyx.lockedTokenAmount.put(i, amount);
                }
                amount -= amount*exy.getTarget().getFee();
            }
        }
        return true;
    }

    public boolean refundHops(Transfer transfer) {
        List<GraphPath<LNVertex, LNEdge>> paths = findShortestPaths(transfer.getSource(), transfer.getRecipient(),
                transfer.getAmount(), null);
        if (paths == null || paths.size() == 0) {
            return false;
        }
        GraphPath<LNVertex, LNEdge> bestPath = paths.get(0);
        List<LNEdge> edges = bestPath.getEdgeList();
        for (LNEdge exy : edges) {
            LNVertex ex = exy.getSource();
            int missingAmount =  transfer.getAmount() - exy.tokenAmount;
            if (hops.contains(ex) && missingAmount>0) {
                exy.tokenAmount += missingAmount;
                System.out.println("Refund hop: " + ex + " amount: " + missingAmount);
            }
        }
        return true;
    }

    public void resetGraph() {
        this.networkGraph = new SimpleDirectedWeightedGraph<LNVertex, LNEdge>(LNEdge.class);
        this.hops = new ArrayList<LNVertex>();
    }

}
