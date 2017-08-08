package io.gridplus.ln;


import io.gridplus.ln.model.LNEdge;
import io.gridplus.ln.model.LNPathValidator;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.model.Transfer;
import org.jgrapht.GraphPath;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class NetworkTopologyTransferTest extends NetworkTopologyTest {

    @Test
    public void testTransfer1() {
        LNVertex v1 = new LNVertex(0);
        LNVertex v2 = new LNVertex(4);
        int amountTransferred = 5;
        Transfer t = new Transfer(v1, v2, amountTransferred, 2, 1);

        LNPathValidator pathValidator = new LNPathValidator(5);

        List<GraphPath<LNVertex, LNEdge>> paths = networkTop.findShortestPaths(v1, v2, 5, pathValidator);

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

        int amount03 = edge03.tokenAmount;
        int amount34 = edge34.tokenAmount;
        int amount30 = edge30.tokenAmount;
        int amount43 = edge43.tokenAmount;

        networkTop.sendTransfer(t);

        assertEquals("V0- Amount03", edge03.tokenAmount, amount03 - amountTransferred);
        assertEquals("V3- Amount03", edge30.tokenAmount, amount30 + amountTransferred);
        assertEquals("V3- Amount34", edge34.tokenAmount, amount34 - amountTransferred);
        assertEquals("V4- Amount34", edge43.tokenAmount, amount43 + amountTransferred);
    }

    @Test
    public void testKShortestPaths3() {
        LNVertex v1 = new LNVertex(0);
        LNVertex v2 = new LNVertex(4);
        int amountTransferred = 2;
        Transfer t = new Transfer(v1, v2, amountTransferred, amountTransferred, 1);

        LNPathValidator pathValidator = new LNPathValidator(amountTransferred);
        List<GraphPath<LNVertex, LNEdge>> paths = networkTop.findShortestPaths(v1, v2, 2, pathValidator);
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


        int amount01 = edge01.tokenAmount;
        int amount10 = edge10.tokenAmount;
        int amount12 = edge12.tokenAmount;
        int amount21 = edge21.tokenAmount;
        int amount23 = edge23.tokenAmount;
        int amount32 = edge32.tokenAmount;
        int amount34 = edge34.tokenAmount;
        int amount43 = edge43.tokenAmount;

        LNEdge edge02 = networkTop.getEdge(new LNVertex(0), new LNVertex(2));
        edge02.status = LNEdge.ChannelStatus.CLOSED;
        LNEdge edge03 = networkTop.getEdge(new LNVertex(0), new LNVertex(3));
        edge03.status = LNEdge.ChannelStatus.CLOSED;

        boolean valid = networkTop.sendTransfer(t);
        assertEquals("Transferred", valid, true);

        assertEquals("V0- Amount01", edge01.tokenAmount, amount01 - amountTransferred);
        assertEquals("V1- Amount10", edge10.tokenAmount, amount10 + amountTransferred);

        double paidFee= amountTransferred * edge12.getSource().fee;
        double amount12New = amount12 - (amountTransferred - paidFee);
        double amount21New = amount21 + (amountTransferred - paidFee);
        double amount23New = amount23 - (amountTransferred - paidFee);
        double amount32New = amount32 + (amountTransferred - paidFee);
        double amount34New = amount34 - (amountTransferred - paidFee);
        double amount43New = amount43 + (amountTransferred - paidFee);


        assertEquals("V1- Amount12", true, Math.abs(edge12.tokenAmount- amount12New) < EPSILON);
        assertEquals("V2- Amount21", true,  Math.abs(edge21.tokenAmount - amount21New) < EPSILON);
        assertEquals("V1- Amount23", true, Math.abs(edge23.tokenAmount- amount23New) < EPSILON);
        assertEquals("V2- Amount32", true,  Math.abs(edge32.tokenAmount - amount32New) < EPSILON);
        assertEquals("V1- Amount34", true, Math.abs(edge34.tokenAmount- amount34New) < EPSILON);
        assertEquals("V2- Amount43", true,  Math.abs(edge43.tokenAmount - amount43New) < EPSILON);

    }

}
