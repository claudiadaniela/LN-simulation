package io.gridplus.ln.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.KShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.alg.shortestpath.PathValidator;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import io.gridplus.ln.model.LNVertex.NetworkStatus;
import io.gridplus.ln.simulator.BlockCounterRunner;

public class NetworkTopology {

    private SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph;
    private Map<LNEdge, Integer> refunds;
    private Map<LNVertex, Integer> fees;

    public NetworkTopology() {
        this.networkGraph = new SimpleDirectedWeightedGraph<LNVertex, LNEdge>(LNEdge.class);
        refunds = new TreeMap<>(new LNEdge.LNEdgeComparator());
    }

    public NetworkTopology(SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph) {
        this.networkGraph = networkGraph;
        refunds = new TreeMap<>(new LNEdge.LNEdgeComparator());
    }

    public LNVertex addNode(int id, double fee, NetworkStatus status, boolean hop) {
        LNVertex vertex = new LNVertex(id, fee);
        vertex.hop = hop;
        vertex.networkStatus = new NetworkStatus(1);
        networkGraph.addVertex(vertex);
        return vertex;
    }

    public void addChannel(LNVertex v1, LNVertex v2, LNEdge.ChannelStatus status, int tokenAmountV1,
                           int tokenAmountV2) {
        LNEdge e12 = networkGraph.addEdge(v1, v2);
        e12.status = status;
        e12.addTokenAmount(tokenAmountV1);
        networkGraph.setEdgeWeight(e12, v1.feePercentage);
        LNEdge e21 = networkGraph.addEdge(v2, v1);
        e21.status = status;
        e21.addTokenAmount(tokenAmountV2);
        networkGraph.setEdgeWeight(e21, v2.feePercentage);
    }

    public List<GraphPath<LNVertex, LNEdge>> findShortestPaths(LNVertex id1, LNVertex id2, int amount,
                                                               PathValidator<LNVertex, LNEdge> validator) {
        KShortestPathAlgorithm<LNVertex, LNEdge> pathsAlg = new KShortestPaths<LNVertex, LNEdge>(networkGraph, 3,
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

    public Set<LNVertex> getVertices() {
        return networkGraph.vertexSet();
    }

    public Set<LNVertex> getHops() {
       return networkGraph.vertexSet().stream().filter(v -> v.hop).collect(Collectors.toSet());
    }

    public SimpleDirectedWeightedGraph<LNVertex, LNEdge> getNetworkGraph() {
        return networkGraph;
    }

    public int getMinAmountOnNodeEdges(LNVertex v1) {
        int currentBlock = BlockCounterRunner.getInstance().currentBlock();
        Set<LNEdge> edges = getEdges(v1);

        int min = Integer.MAX_VALUE;
        for (LNEdge e : edges) {
            int availableAmount = e.getAvailableAmount(currentBlock);
            if (availableAmount < min) {
                min = availableAmount;
            }
        }
        return min != Integer.MAX_VALUE ? min : 0;
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
        int currentBlock = BlockCounterRunner.getInstance().currentBlock();
        int amount = transfer.getAmount();
        for (LNEdge exy : edges) {
            LNVertex ex = exy.getSource();
            LNVertex ey = exy.getTarget();
            LNEdge eyx = getEdge(ey, ex);

            if (eyx != null) {
                synchronized (eyx) {
                    exy.addTokenAmount(-amount);
                    eyx.addTokenAmount(amount);
                    for (int i = currentBlock; i < lockedTime; i++) {
                        if (eyx.lockedTokenAmount.containsKey(i)) {
                            int ammountExisting = eyx.lockedTokenAmount.get(i);
                            eyx.lockedTokenAmount.put(i, amount + ammountExisting);
                        } else {
                            eyx.lockedTokenAmount.put(i, amount);
                        }
                    }
                    amount -= amount * exy.getTarget().feePercentage;
                    lockedTime--;
                }
            }
        }
        return true;
    }

    /**
     * TODO: sync mechanisms
     *
     * @param transfer
     * @return
     */
    public boolean refundHops(Transfer transfer) {
        List<GraphPath<LNVertex, LNEdge>> paths = findShortestPaths(transfer.getSource(), transfer.getRecipient(),
                transfer.getAmount(), null);
        if (paths == null || paths.size() == 0) {
            return false;
        }
        GraphPath<LNVertex, LNEdge> bestPath = paths.get(0);
        List<LNEdge> edges = bestPath.getEdgeList();
        int currentBlock = BlockCounterRunner.getInstance().currentBlock();

        for (LNEdge exy : edges) {
            LNVertex ex = exy.getSource();
            int missingAmount = transfer.getAmount() - exy.getAvailableAmount(currentBlock);
            if (ex.hop && missingAmount > 0) {
                refund(exy, missingAmount);
            }
        }
        return true;
    }

    private void refund(LNEdge hopEdge, int amount) {
        if(!hopEdge.getSource().hop){return;}
        System.out.println("Refund hop: " + hopEdge.getSource() + " amount: " + amount);

        hopEdge.addTokenAmount(amount);
        if (refunds.containsKey(hopEdge)) {
            amount += refunds.get(hopEdge);
        }
        refunds.put(hopEdge, amount);
   }

    public Map<LNEdge, Integer> getRefunds(){
        return  refunds;
    }

    public Map<String, Map<String, Integer>> getNodesState() {
        Set<LNVertex> vertexSet = networkGraph.vertexSet();
        Map<String, Map<String, Integer>> networkState = new HashMap<String, Map<String, Integer>>();
        for (LNVertex v : vertexSet) {
            Map<String, Integer> edgeState = new HashMap<>();
            networkState.put(v.toString(), edgeState);

            Set<LNEdge> edges = networkGraph.outgoingEdgesOf(v);
            for (LNEdge e : edges) {
                edgeState.put(e.getTarget().toString(), e.getTotalAmount());
            }
        }
        return networkState;
    }

}
