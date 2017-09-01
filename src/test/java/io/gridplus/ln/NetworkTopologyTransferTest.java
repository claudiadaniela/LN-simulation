package io.gridplus.ln;


import static org.junit.Assert.assertEquals;

import java.util.List;

import org.jgrapht.GraphPath;
import org.junit.Test;

import io.gridplus.ln.model.LNEdge;
import io.gridplus.ln.model.LNPathValidator;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.model.Transfer;

public class NetworkTopologyTransferTest extends NetworkTopologyTest {

    @Test
    public void testTransfer1() {
        LNVertex v1 = new LNVertex(0);
        LNVertex v2 = new LNVertex(4);
        int amountTransferred = 5;
        Transfer t = new Transfer(v1, v2, amountTransferred);

        LNPathValidator pathValidator = new LNPathValidator(5);

        List<GraphPath<LNVertex, LNEdge>> paths = networkTop.findShortestPaths(v1, v2,  pathValidator);

        assertEquals("Paths found", 1, paths.size());
        GraphPath<LNVertex, LNEdge> path0 = paths.get(0);

        assertEquals("VStart", path0.getStartVertex(), new LNVertex(0));
        assertEquals("VEnd", path0.getEndVertex(), new LNVertex(4));

        List<LNEdge> edges = path0.getEdgeList();
        LNEdge edge03 = edges.get(0);
        LNEdge edge34 = edges.get(1);
        LNEdge edge30 = networkTop.getEdge(edge03.getTarget(), edge03.getSource());
        LNEdge edge43 = networkTop.getEdge(edge34.getTarget(), edge34.getSource());

        assertEquals("Path0 size", 2, edges.size());
        assertEquals("V0-Source", edge03.getSource(), new LNVertex(0));
        assertEquals("V0-Target", edge03.getTarget(), new LNVertex(3));

        assertEquals("V1-Source", edge34.getSource(), new LNVertex(3));
        assertEquals("V1-Target", edge34.getTarget(), new LNVertex(4));

        double amount03 = edge03.getTotalAmount();
        double amount34 = edge34.getTotalAmount();
        double amount30 = edge30.getTotalAmount();
        double amount43 = edge43.getTotalAmount();

        networkTop.sendTransfer(t);

        assertEquals("V0- Amount03", Math.abs(edge03.getTotalAmount()- (amount03 - amountTransferred))<EPSILON, true);
        assertEquals("V3- Amount03", Math.abs(edge30.getTotalAmount()-(amount30 + amountTransferred))< EPSILON, true);
        assertEquals("V3- Amount34", Math.abs(edge34.getTotalAmount()- (amount34 - amountTransferred)) <EPSILON, true);
        assertEquals("V4- Amount34", Math.abs(edge43.getTotalAmount()- (amount43 + amountTransferred))<EPSILON, true);
    }

    @Test
    public void testKShortestPaths3() {
        LNVertex v1 = new LNVertex(0);
        LNVertex v2 = new LNVertex(4);
        int amountTransferred = 2;
        Transfer t = new Transfer(v1, v2, amountTransferred);

        LNPathValidator pathValidator = new LNPathValidator(amountTransferred);
        List<GraphPath<LNVertex, LNEdge>> paths = networkTop.findShortestPaths(v1, v2,  pathValidator);
        assertEquals("Paths found", 3, paths.size());

        GraphPath<LNVertex, LNEdge> path2 = paths.get(2);

        List<LNEdge> edges = path2.getEdgeList();
        LNEdge edge01 = edges.get(0);
        LNEdge edge12 = edges.get(1);
        LNEdge edge23 = edges.get(2);
        LNEdge edge34 = edges.get(3);

        LNEdge edge10 = networkTop.getEdge(edge01.getTarget(), edge01.getSource());
        LNEdge edge21 = networkTop.getEdge(edge12.getTarget(), edge12.getSource());
        LNEdge edge32 = networkTop.getEdge(edge23.getTarget(), edge23.getSource());
        LNEdge edge43 = networkTop.getEdge(edge34.getTarget(), edge34.getSource());

        assertEquals("Path1 size", 4, edges.size());
        assertEquals("V0-Source p2", edges.get(0).getSource(), new LNVertex(0));
        assertEquals("V0-Target p2", edges.get(0).getTarget(), new LNVertex(1));

        assertEquals("V1-Source p2", edges.get(1).getSource(), new LNVertex(1));
        assertEquals("V1-Target p2", edges.get(1).getTarget(), new LNVertex(2));

        assertEquals("V2-Source p2", edges.get(2).getSource(), new LNVertex(2));
        assertEquals("V2-Target p2", edges.get(2).getTarget(), new LNVertex(3));

        assertEquals("V3-Source p2", edges.get(3).getSource(), new LNVertex(3));
        assertEquals("V3-Target p2", edges.get(3).getTarget(), new LNVertex(4));


        double amount01 = edge01.getTotalAmount();
        double amount10 = edge10.getTotalAmount();
        double amount12 = edge12.getTotalAmount();
        double amount21 = edge21.getTotalAmount();
        double amount23 = edge23.getTotalAmount();
        double amount32 = edge32.getTotalAmount();
        double amount34 = edge34.getTotalAmount();
        double amount43 = edge43.getTotalAmount();

        LNEdge edge02 = networkTop.getEdge(new LNVertex(0), new LNVertex(2));
        edge02.status = LNEdge.ChannelStatus.CLOSED;
        LNEdge edge03 = networkTop.getEdge(new LNVertex(0), new LNVertex(3));
        edge03.status = LNEdge.ChannelStatus.CLOSED;

        boolean valid = networkTop.sendTransfer(t);
        assertEquals("Transferred", valid, true);

        assertEquals("V0- Amount01", Math.abs(edge01.getTotalAmount()-( amount01 - amountTransferred)) <EPSILON, true);
        assertEquals("V1- Amount10", Math.abs(edge10.getTotalAmount()-( amount10 + amountTransferred))<EPSILON, true);

        double paidFee = amountTransferred * edge12.getSource().feePercentage;
        double amount12New = amount12 - (amountTransferred - paidFee);
        double amount21New = amount21 + (amountTransferred - paidFee);
        double amount23New = amount23 - (amountTransferred - paidFee);
        double amount32New = amount32 + (amountTransferred - paidFee);
        double amount34New = amount34 - (amountTransferred - paidFee);
        double amount43New = amount43 + (amountTransferred - paidFee);


        assertEquals("V1- Amount12", true, Math.abs(edge12.getTotalAmount() - amount12New) < EPSILON);
        assertEquals("V2- Amount21", true, Math.abs(edge21.getTotalAmount() - amount21New) < EPSILON);
        assertEquals("V1- Amount23", true, Math.abs(edge23.getTotalAmount() - amount23New) < EPSILON);
        assertEquals("V2- Amount32", true, Math.abs(edge32.getTotalAmount() - amount32New) < EPSILON);
        assertEquals("V1- Amount34", true, Math.abs(edge34.getTotalAmount() - amount34New) < EPSILON);
        assertEquals("V2- Amount43", true, Math.abs(edge43.getTotalAmount() - amount43New) < EPSILON);

    }

}
