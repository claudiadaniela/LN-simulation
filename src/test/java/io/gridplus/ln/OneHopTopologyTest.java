package io.gridplus.ln;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jdk.nashorn.internal.objects.NativeRegExp;
import org.junit.Test;

import io.gridplus.ln.model.LNEdge;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.model.Transfer;
import io.gridplus.ln.network.topology.NetworkTopology;
import io.gridplus.ln.network.topology.factory.NetworkTopologyAbstractFactory;
import io.gridplus.ln.simulator.BlockCounterRunner;
import io.gridplus.ln.simulator.NetworkClientRunner;
import io.gridplus.ln.simulator.utils.CSVReader;

public class OneHopTopologyTest {

    private static final double EPSILON = 0.000001;

    private Map<LNEdge, Double> refunds;

    @Test
    public void testTransfers() {
        NetworkTopologyAbstractFactory topoFactory = NetworkTopologyAbstractFactory
                .getInstance(NetworkTopologyAbstractFactory.Type.FILE);
        NetworkTopology networkTop = topoFactory.createTopology("./src/test/resources/test-one-hop/test-one-hop.xml");
        networkTop.activateRefund();

        refunds = new TreeMap<>(new LNEdge.LNEdgeComparator());

        List<Transfer> trasnfers = CSVReader.readTransfers("./src/test/resources/test-one-hop/test-transfers.csv");

        BlockCounterRunner clock = BlockCounterRunner.getInstance();
        clock.setSimulationSteps(1);
        NetworkClientRunner networkClientRunner = new NetworkClientRunner(1, networkTop);

        for (Transfer t : trasnfers) {
            networkClientRunner.addTransfer(t);
        }
        new Thread(networkClientRunner).start();
        new Thread(clock).start();


        while (networkClientRunner.running()) {
        }

        topoFactory = NetworkTopologyAbstractFactory
                .getInstance(NetworkTopologyAbstractFactory.Type.FILE);
        NetworkTopology networkTop2 = topoFactory.createTopology("./src/test/resources/test-one-hop/test-one-hop.xml");
        networkTop2.activateRefund();
        updateTopo2(networkTop2, trasnfers);
        LNVertex hop2 = networkTop2.getHops().iterator().next();

        LNVertex hop1 = networkTop.getHops().iterator().next();
        List<LNVertex> orderedSet1 = new ArrayList<>(networkTop.getVertices());
        List<LNVertex> orderedSet2 = new ArrayList<>(networkTop2.getVertices());
        Collections.sort(orderedSet1, new LNVertex.LNVertexComparator());
        Collections.sort(orderedSet2, new LNVertex.LNVertexComparator());
        assertEquals("vertex set of same size", orderedSet1.size(), orderedSet2.size());

        for (int i = 0; i < orderedSet1.size(); i++) {
            LNVertex v1 = orderedSet1.get(i);
            LNVertex v2 = orderedSet2.get(i);

            if (v1.hop || v2.hop) {
                continue;
            }

            LNEdge edgeV1H = networkTop.getEdge(v1, hop1);
            LNEdge edgeHV1 = networkTop.getEdge(hop1, v1);


            LNEdge edgeV2H = networkTop2.getEdge(v2, hop2);
            LNEdge edgeHV2 = networkTop2.getEdge(hop2, v2);

            assertEquals("edge amount Direct", Math.abs(edgeV1H.getTotalAmount()- edgeV2H.getTotalAmount()) <EPSILON, true);
            assertEquals("edge amount Direct", Math.abs(edgeHV1.getTotalAmount()- edgeHV2.getTotalAmount())<EPSILON, true);
        }

        Map<LNEdge, Double> refundsTopo = networkTop.getRefunds();
        for (Map.Entry<LNEdge, Double> entry : refunds.entrySet()) {
            assertEquals("refunds on edge", entry.getValue(), refundsTopo.get(entry.getKey()));
        }
    }


    @Test
    public void testHop0() {
        NetworkTopologyAbstractFactory topoFactory = NetworkTopologyAbstractFactory
                .getInstance(NetworkTopologyAbstractFactory.Type.FILE);
        NetworkTopology networkTop = topoFactory.createTopology("./src/test/resources/test-one-hop/test-one-hop.xml");
        networkTop.activateRefund();

        refunds = new TreeMap<>(new LNEdge.LNEdgeComparator());

        List<Transfer> trasnfers = CSVReader.readTransfers("./src/test/resources/test-one-hop/test-transfers-Hop0.csv");

        BlockCounterRunner clock = BlockCounterRunner.getInstance();
        clock.setSimulationSteps(1);
        NetworkClientRunner networkClientRunner = new NetworkClientRunner(1, networkTop);
        for (Transfer t : trasnfers) {
            networkClientRunner.addTransfer(t);
        }
        new Thread(networkClientRunner).start();
        new Thread(clock).start();


        while (networkClientRunner.running()) {
        }

        topoFactory = NetworkTopologyAbstractFactory
                .getInstance(NetworkTopologyAbstractFactory.Type.FILE);
        NetworkTopology networkTop2 = topoFactory.createTopology("./src/test/resources/test-one-hop/test-one-hop.xml");
        networkTop2.activateRefund();
        updateTopo2(networkTop2, trasnfers);
        networkTop2.activateRefund();
        LNEdge edge = networkTop2.getEdge(new LNVertex(1), new LNVertex(0));


        assertEquals("edge amount Reverse", Math.abs(edge.getTotalAmount()- 21024.96)<EPSILON, true);

        edge = networkTop2.getEdge(new LNVertex(0), new LNVertex(1));
        assertEquals("edge amount Direct", Math.abs(edge.getTotalAmount()-788.01) <EPSILON, true);
        assertEquals("hop refund", Math.abs(networkTop.getRefunds().get(edge)-2972.97)<EPSILON, true);
        assertEquals("total flow",Math.abs( networkTop.getTotalFlow().get(new LNVertex(0))- 3761)<EPSILON, true);
        assertEquals("total fees",  Math.abs(networkTop.getFees().get(new LNVertex(0)).doubleValue() - 0.05) < EPSILON, true);
    }

    @Test
    public void testHourOne() {
        NetworkTopologyAbstractFactory topoFactory = NetworkTopologyAbstractFactory
                .getInstance(NetworkTopologyAbstractFactory.Type.FILE);
        NetworkTopology networkTop = topoFactory.createTopology("./src/test/resources/test-one-hour/graph.xml");
        networkTop.activateRefund();

        refunds = new TreeMap<>(new LNEdge.LNEdgeComparator());

        List<Transfer> trasnfers = CSVReader.readTransfers("./src/test/resources/test-one-hour/transfers.csv");
        BlockCounterRunner clock = BlockCounterRunner.getInstance();
        clock.setSimulationSteps(1);
        NetworkClientRunner networkClientRunner = new NetworkClientRunner(1, networkTop);
        for (Transfer t : trasnfers) {
            networkClientRunner.addTransfer(t);
        }
        new Thread(networkClientRunner).start();
        new Thread(clock).start();

        while (networkClientRunner.running()) {
        }


        double amount = 0;
        for (Transfer t : trasnfers) {
            amount += t.getAmount();
        }
        double amountSim = 0;
        for (Map.Entry<LNEdge, Double> e : networkTop.getRefunds().entrySet()) {
            amountSim += e.getValue();
        }
        double totalFlow = networkTop.getTotalFlow().get(new LNVertex(0));
        System.out.println("total refunds" + amountSim);
        System.out.println("total flow" + totalFlow);
        System.out.println("total amount" + amount);
        assertEquals("Amount of funds flowing through hop", Math.abs(totalFlow- amount)< EPSILON , true);
        assertEquals("Amount of funds flowing through hop", amountSim <= amount, true);


    }

    private void updateTopo2(NetworkTopology networkTop2, List<Transfer> transfers) {

        LNVertex hop2 = networkTop2.getHops().iterator().next();

        for (Transfer t : transfers) {
            LNEdge edgeD = networkTop2.getEdge(t.getSource(), t.getRecipient());
            if (edgeD != null) {
                LNEdge edgeR = networkTop2.getEdge(t.getRecipient(), t.getSource());
                updateTokenAmount(edgeD, edgeD.getSource(), t.getAmount());
                edgeD.addTokenAmount(-t.getAmount());
                edgeR.addTokenAmount(t.getAmount());
                updateLockedAmount(edgeR, t.getAmount(), 1);
            } else {
                LNEdge edge1D = networkTop2.getEdge(t.getSource(), hop2);
                LNEdge edge1R = networkTop2.getEdge(hop2, t.getSource());
                LNEdge edge2D = networkTop2.getEdge(hop2, t.getRecipient());
                LNEdge edge2R = networkTop2.getEdge(t.getRecipient(), hop2);
                double amount = t.getAmount();

                edge1D.addTokenAmount(-amount);
                edge1R.addTokenAmount(amount);
                updateLockedAmount(edge1R, amount, 2);

                amount -=  hop2.fee;
                updateTokenAmount(edge2D, hop2, t.getAmount());
                edge2D.addTokenAmount(-amount);
                edge2R.addTokenAmount(amount);
                updateLockedAmount(edge2R, amount, 2);
            }
        }
    }

    private void updateTokenAmount(LNEdge edge, LNVertex source, double amount) {
        double missingAmount = amount - edge.getAvailableAmount(0);
        if (source.hop && missingAmount > 0) {
            edge.addTokenAmount(missingAmount);
            if (refunds.containsKey(edge)) {
                missingAmount += refunds.get(edge);
            }
            refunds.put(edge, missingAmount);
        }
    }

    private static void updateLockedAmount(LNEdge edge, double amount, int locktime) {
        for (int i = 0; i < locktime; i++) {
            if (edge.lockedTokenAmount.containsKey(i)) {
                double ammountExisting = edge.lockedTokenAmount.get(i);
                edge.lockedTokenAmount.put(i, amount + ammountExisting);
            } else {
                edge.lockedTokenAmount.put(i, amount);
            }
        }
    }

}
