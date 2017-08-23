package io.gridplus.ln.model;

import io.gridplus.ln.model.LNVertex.NetworkStatus;
import io.gridplus.ln.simulator.BlockCounterRunner;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.KShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.alg.shortestpath.PathValidator;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.*;

public class NetworkTopology {

    private SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph;

    public NetworkTopology() {
        this.networkGraph = new SimpleDirectedWeightedGraph<LNVertex, LNEdge>(LNEdge.class);
    }

    public NetworkTopology(SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph) {
        this.networkGraph = networkGraph;
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
        e12.changeTokenAmount(tokenAmountV1);
        networkGraph.setEdgeWeight(e12, v1.feePercentage);
        LNEdge e21 = networkGraph.addEdge(v2, v1);
        e21.status = status;
        e21.changeTokenAmount(tokenAmountV2);
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
        Set<LNVertex> vertexSet = new HashSet<>();

        for (LNVertex v : networkGraph.vertexSet()) {
            if (v.hop) {
                vertexSet.add(v);
            }
        }
        return vertexSet;
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
                    exy.changeTokenAmount(-amount);
                    eyx.changeTokenAmount(amount);
                    for (int i = currentBlock; i < lockedTime; i++) {
                        if(eyx.lockedTokenAmount.containsKey(i)) {
                            int ammountExisting = eyx.lockedTokenAmount.get(i);
                            eyx.lockedTokenAmount.put(i, amount+ammountExisting);
                        }else{eyx.lockedTokenAmount.put(i, amount);}
                    }
                    amount -= amount * exy.getTarget().feePercentage;
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
                exy.changeTokenAmount(missingAmount);
                System.out.println("Refund hop: " + ex + " amount: " + missingAmount);
            }
        }
        return true;
    }

    public Map<String, Map<String, Integer>> getNodesState() {
        Set<LNVertex> vertexSet = networkGraph.vertexSet();
        Map<String, Map<String, Integer>> networkState = new HashMap<String, Map<String, Integer>>();
        int currentBlock = BlockCounterRunner.getInstance().currentBlock();

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
