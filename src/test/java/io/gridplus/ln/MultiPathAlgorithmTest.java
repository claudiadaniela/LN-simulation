package io.gridplus.ln;

import static org.junit.Assert.assertEquals;



import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.junit.Test;

import io.gridplus.ln.model.LNEdge;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.multipath.MinCostMaxFlowAlgorithm;

public class MultiPathAlgorithmTest extends NetworkTopologyTest {


    @Test
    public void testMultiPath() {
        SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph = networkTop.getNetworkGraph();
         MinCostMaxFlowAlgorithm flowAlg = MinCostMaxFlowAlgorithm.getInstance(networkGraph);

        double[] ret = flowAlg.getMaxFlow(0, 4);
        assertEquals("Max flow", true, ret[0] == 10);
        assertEquals("Min cost", true, Math.abs(0.1 - ret[1]) < EPSILON);
    }
}
