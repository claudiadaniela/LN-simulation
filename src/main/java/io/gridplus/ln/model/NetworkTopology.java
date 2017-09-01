package io.gridplus.ln.model;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.KShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.alg.shortestpath.PathValidator;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import io.gridplus.ln.model.LNVertex.NetworkStatus;
import io.gridplus.ln.simulator.BlockCounterRunner;

public class NetworkTopology {
    private static final Logger LOGGER = Logger.getLogger(NetworkTopology.class.getName());
    private SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph;
    private Map<LNEdge, Double> refunds;
    private Map<LNVertex, Double> totalFlow;
    private Map<LNVertex, Double> fees;
    private Map<LNEdge, Double> invariantEdges;
    private boolean REFUND_ACTIVE;

    public NetworkTopology() {
        this.networkGraph = new SimpleDirectedWeightedGraph<>(LNEdge.class);
        this.refunds = new TreeMap<>(new LNEdge.LNEdgeComparator());
        this.invariantEdges = new TreeMap<>(new LNEdge.LNEdgeComparator());
        this.fees = new HashMap<>();
        this.totalFlow = new HashMap<>();
    }

    public NetworkTopology(SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph) {
        this.networkGraph = networkGraph;
        this.refunds = new TreeMap<>(new LNEdge.LNEdgeComparator());
        this.invariantEdges = new TreeMap<>(new LNEdge.LNEdgeComparator());
        this.fees = new HashMap<>();
        this.totalFlow = new HashMap<>();
    }

    public LNVertex addNode(int id, double fee, NetworkStatus status, boolean hop) {
        LNVertex vertex = new LNVertex(id, fee);
        vertex.hop = hop;
        vertex.networkStatus = new NetworkStatus(1);
        networkGraph.addVertex(vertex);
        return vertex;
    }

    public void addChannel(LNVertex v1, LNVertex v2, LNEdge.ChannelStatus status, double tokenAmountV1,
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

    public List<GraphPath<LNVertex, LNEdge>> findShortestPaths(LNVertex id1, LNVertex id2,
                                                               PathValidator<LNVertex, LNEdge> validator) {
        KShortestPathAlgorithm<LNVertex, LNEdge> pathsAlg = new KShortestPaths<LNVertex, LNEdge>(networkGraph, 3,
                validator);
        List<GraphPath<LNVertex, LNEdge>> paths = pathsAlg.getPaths(id1, id2);
        Collections.sort(paths, new LNPathComparator());
        return paths;
    }

    public boolean sendTransfer(Transfer transfer) {
        if (!isWellFormed() && !REFUND_ACTIVE) {
            LOGGER.log(Level.SEVERE, "Channel tokens are not consistent");
        }
        List<GraphPath<LNVertex, LNEdge>> paths = findShortestPaths(transfer.getSource(), transfer.getRecipient(), new LNPathValidator(transfer.getAmount()));
        if (paths == null || paths.size() == 0) {
            return false;
        }
        GraphPath<LNVertex, LNEdge> bestPath = paths.get(0);
        List<LNEdge> edges = bestPath.getEdgeList();
        int lockedTime = bestPath.getLength();
        int currentBlock = BlockCounterRunner.getInstance().currentBlock();
        double amount = transfer.getAmount();
        double fee = 0;
        for (LNEdge exy : edges) {
            LNVertex ex = exy.getSource();
            LNVertex ey = exy.getTarget();
            LNEdge eyx = getEdge(ey, ex);


            if (eyx != null) {
                synchronized (eyx) {
                    updateFee(fee, ex);
                    exy.addTokenAmount(-amount);
                    eyx.addTokenAmount(amount);
                    for (int i = currentBlock; i < lockedTime; i++) {
                        if (eyx.lockedTokenAmount.containsKey(i)) {
                            double ammountExisting = eyx.lockedTokenAmount.get(i);
                            eyx.lockedTokenAmount.put(i, amount + ammountExisting);
                        } else {
                            eyx.lockedTokenAmount.put(i, amount);
                        }
                    }
                    updateTotalFlow(ey, amount);
                    fee = amount * exy.getTarget().feePercentage;
                    amount -= fee;
                    lockedTime--;
                }
            }
        }
        isWellFormed();
        return true;
    }


    /**
     * TODO: sync mechanisms
     * Refund with the total amount on each hop.
     * During the transfer , the actual amount sent on edges will be smaller,
     * depending on the fee of the hops it passes through.
     */
    public boolean refundHops(Transfer transfer) {
        if (!REFUND_ACTIVE) {
            return false;
        }
        List<GraphPath<LNVertex, LNEdge>> paths = findShortestPaths(transfer.getSource(), transfer.getRecipient(),null);
        if (paths == null || paths.size() == 0) {
            return false;
        }
        GraphPath<LNVertex, LNEdge> bestPath = paths.get(0);
        List<LNEdge> edges = bestPath.getEdgeList();
        int currentBlock = BlockCounterRunner.getInstance().currentBlock();
        for (LNEdge exy : edges) {
            LNVertex ex = exy.getSource();
            double missingAmount = transfer.getAmount() - exy.getAvailableAmount(currentBlock);
            if (ex.hop && missingAmount > 0) {
                refund(exy, missingAmount);
            }
        }
        return true;
    }

    private void refund(LNEdge hopEdge, double amount) {
        if (!hopEdge.getSource().hop) {
            return;
        }
        LOGGER.log(Level.INFO, "Refund hop: " + hopEdge.getSource() + " amount: " + amount);

        hopEdge.addTokenAmount(amount);
        if (refunds.containsKey(hopEdge)) {
            amount += refunds.get(hopEdge);
        }
        refunds.put(hopEdge, amount);
    }

    public Map<String, Map<String, Double>> getNodesState() {
        Set<LNVertex> vertexSet = networkGraph.vertexSet();
        Map<String, Map<String, Double>> networkState = new HashMap<String, Map<String, Double>>();
        for (LNVertex v : vertexSet) {
            Map<String, Double> edgeState = new HashMap<>();
            networkState.put(v.toString(), edgeState);

            Set<LNEdge> edges = networkGraph.outgoingEdgesOf(v);
            for (LNEdge e : edges) {
                edgeState.put(e.getTarget().toString(), e.getTotalAmount());
            }
        }
        return networkState;
    }


    private void updateFee(double fee, LNVertex v) {
        if (fees.containsKey(v)) {
            fee += fees.get(v);
        }
        fees.put(v, fee);
    }

    private void updateTotalFlow(LNVertex v, double amount) {
        if (!v.hop) {
            return;
        }
        if (totalFlow.containsKey(v)) {
            amount += totalFlow.get(v);
        }
        totalFlow.put(v, amount);
    }

    public Map<LNEdge, Double> getRefunds() {
        return refunds;
    }

    public Map<LNVertex, Double> getFees() {
        return fees;
    }

    public Map<LNVertex, Double> getTotalFlow() {
        return totalFlow;
    }

    public void initInvariant() {
        invariantEdges = getTotalBiEdgeAmount();
    }

    private Map<LNEdge, Double> getTotalBiEdgeAmount() {
        Map<LNEdge, Double> bidirEdges = new TreeMap<>(new LNEdge.LNEdgeComparator());
        Set<LNEdge> edges = networkGraph.edgeSet();
        Set<LNEdge> inverse = new HashSet<>();
        for (LNEdge edge : edges) {
            if (!inverse.contains(edge)) {
                LNEdge edgeR = networkGraph.getEdge(edge.getTarget(), edge.getSource());
                double totalAmountEdge = edge.getTotalAmount() + edgeR.getTotalAmount();
                inverse.add(edgeR);
                bidirEdges.put(edge, totalAmountEdge);
            }
        }
        return bidirEdges;
    }

    public boolean isWellFormed() {
        if (REFUND_ACTIVE) {
            return false;
        }
        Map<LNEdge, Double> bidirEdges = getTotalBiEdgeAmount();
        for (Map.Entry<LNEdge, Double> entry : bidirEdges.entrySet()) {
            if (!entry.getValue().equals(invariantEdges.get(entry.getKey()))) {
                return false;
            }
        }
        return true;
    }

    public void activateRefund() {
        REFUND_ACTIVE = true;
    }

    public LNEdge getEdge(LNVertex v1, LNVertex v2) {
        return networkGraph.getEdge(v1, v2);
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

}
