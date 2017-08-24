package io.gridplus.ln;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.junit.Test;

import io.gridplus.ln.model.LNEdge;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.multipath.MinCostMaxFlowAlgorithm;;

public class MultiPathAlgorithmTest extends NetworkTopologyTest {
	private MinCostMaxFlowAlgorithm flowAlg = new MinCostMaxFlowAlgorithm();
	private static final int noNodes = 5;


	@Test
	public void testMultiPath() {
		SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph = networkTop.getNetworkGraph();

		int[][] capacity = new int[noNodes][noNodes];
		double[][] fee = new double[noNodes][noNodes];

		for (int i = 0; i < noNodes; i++) {
			for (int j = 0; j < i; j++) {
				Set<LNEdge> edgeSet = networkGraph.getAllEdges(new LNVertex(i), new LNVertex(j));
				for (LNEdge e : edgeSet) {
					capacity[i][j] = e.getTotalAmount();
					fee[i][j] =  e.getWeight();
				}

				Set<LNEdge> edgeSet2 = networkGraph.getAllEdges(new LNVertex(j), new LNVertex(i));
				for (LNEdge e : edgeSet2) {
					capacity[j][i] = e.getTotalAmount();
					fee[j][i] =  e.getWeight();
				}
			}
		}

		double[] ret = flowAlg.getMaxFlow(capacity, fee, 0, 4);
		assertEquals("Max flow", true, ret[0] == 10);
		assertEquals("Min cost", true, Math.abs( 0.1- ret[1]) < EPSILON );
	}
}
