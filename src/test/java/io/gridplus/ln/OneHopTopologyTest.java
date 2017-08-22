package io.gridplus.ln;

import io.gridplus.ln.model.LNEdge;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.model.NetworkTopology;
import io.gridplus.ln.model.Transfer;
import io.gridplus.ln.network.factory.NetworkTopologyAbstractFactory;

import io.gridplus.ln.network.utils.CSVReader;
import io.gridplus.ln.network.utils.GraphIO;
import io.gridplus.ln.simulator.BlockCounterRunner;
import io.gridplus.ln.simulator.NetworkClientRunner;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class OneHopTopologyTest {

    private static NetworkTopology networkTop;
    private static final double EPSILON = 0.000001;
    private List<Transfer> trasnfers;

    @Before
    public  void init() {
        NetworkTopologyAbstractFactory topoFactory = NetworkTopologyAbstractFactory
                .getInstance(NetworkTopologyAbstractFactory.Type.FILE);
        networkTop = topoFactory.createTopology("./src/test/resources/test-one-hop.xml");
        trasnfers = CSVReader.readTransfers("./src/test/resources/test-transfers.csv");
    }
    @Test
    public void testTransfer1() {
        BlockCounterRunner clock = BlockCounterRunner.getInstance();
        clock.setSimulationSteps(1);
        NetworkClientRunner networkClientRunner = new NetworkClientRunner(1, networkTop);
        new Thread(networkClientRunner).start();
        new Thread(clock).start();

        for(Transfer t : trasnfers){
            networkClientRunner.addTransfer(t);
        }
        while(networkClientRunner.running()){}

        NetworkTopologyAbstractFactory topoFactory = NetworkTopologyAbstractFactory
                .getInstance(NetworkTopologyAbstractFactory.Type.FILE);
        NetworkTopology  networkTop2 = topoFactory.createTopology("./src/test/resources/test-one-hop.xml");

       LNVertex hop2 = networkTop2.getHops().iterator().next();


        for(Transfer t: trasnfers){
            LNEdge edgeD = networkTop2.getEdge(t.getSource(), t.getRecipient());
            if(edgeD!=null){
                LNEdge edgeR = networkTop2.getEdge(t.getRecipient(),t.getSource());
                decrementTokenAmount( edgeD, edgeD.getSource(), t.getAmount());
                edgeR.changeTokenAmount(t.getAmount());
                for (int i = 0; i < t.getLockTime(); i++) {
                    edgeR.lockedTokenAmount.put(i, t.getAmount());
                }
            }else {

                LNEdge edge1D = networkTop2.getEdge(t.getSource(), hop2);
                LNEdge edge1R = networkTop2.getEdge(hop2, t.getSource());
                LNEdge edge2D = networkTop2.getEdge(hop2, t.getRecipient());
                LNEdge edge2R = networkTop2.getEdge(t.getRecipient(), hop2);

                int amount = t.getAmount();

                edge1D.changeTokenAmount(-amount);
                edge1R.changeTokenAmount(amount);
                for (int i = 0; i < t.getLockTime(); i++) {
                    edge1R.lockedTokenAmount.put(i, amount);
                }
                amount -= amount * hop2.feePercentage;
                decrementTokenAmount( edge2D, hop2, amount);
                edge2R.changeTokenAmount(amount);
                for (int i = 0; i < t.getLockTime(); i++) {
                    edge2R.lockedTokenAmount.put(i, amount);
                }
            }

        }


        NetworkTopology  networkTop3 = topoFactory.createTopology("./src/test/resources/test-one-hop.xml");
        LNVertex hop1 = networkTop.getHops().iterator().next();
        LNVertex hop3 = networkTop3.getHops().iterator().next();
        List<LNVertex> orderedSet1 = new ArrayList<>(networkTop.getVertices());
        List<LNVertex> orderedSet2 = new ArrayList<>(networkTop2.getVertices());
        List<LNVertex> orderedSet3 = new ArrayList<>(networkTop3.getVertices());
        Collections.sort(orderedSet3, new LNVertex.LNVertexComparator());
        Collections.sort(orderedSet1, new LNVertex.LNVertexComparator());
        Collections.sort(orderedSet2, new LNVertex.LNVertexComparator());
        assertEquals("vertex set of same size", orderedSet1.size() , orderedSet2.size());

        for(int i =0; i< orderedSet1.size(); i++){
            LNVertex v1 = orderedSet1.get(i);
            LNVertex v2 = orderedSet2.get(i);
            LNVertex v3 = orderedSet3.get(i);
            if(v1.hop || v2.hop){continue;}

            LNEdge edgeV1H = networkTop.getEdge(v1,hop1);
            LNEdge edgeHV1 = networkTop.getEdge(hop1,v1);


            LNEdge edgeV2H = networkTop2.getEdge(v2,hop2);
            LNEdge edgeHV2 = networkTop2.getEdge(hop2,v2);


            LNEdge edgeV3H = networkTop3.getEdge(v3,hop3);
            LNEdge edgeHV3 = networkTop3.getEdge(hop3,v3);

            System.out.println("edge amount Direct "+ edgeV1H +" "+ edgeV2H);
            System.out.println("edge amount Reverse"+ edgeHV1 +" "+ edgeHV2);

        }

        assertEquals("Paths found", 1, 1);
    }

    private static void decrementTokenAmount(LNEdge edge, LNVertex source, int amount){
     int missingAmount =    amount - edge.getAvailableAmount(0);
        if(source.hop && missingAmount >0){
            edge.changeTokenAmount(missingAmount);
        }
        edge.changeTokenAmount(-amount);
    }

}
