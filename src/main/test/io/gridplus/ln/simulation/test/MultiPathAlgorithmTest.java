package io.gridplus.ln.simulation.test;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.junit.Before;
import org.junit.Test;

import io.gridplus.ln.simulation.model.LNEdge;
import io.gridplus.ln.simulation.model.LNVertex;
import io.gridplus.ln.simulation.multipath.MinCostMaxFlowAlgorithm;
import io.gridplus.ln.simulation.network.NetworkTopology;;

public class MultiPathAlgorithmTest {
	private NetworkTopology networkTop;
	private MinCostMaxFlowAlgorithm flowAlg = new MinCostMaxFlowAlgorithm();
	private static final int noNodes = 5;

	@Before
	public void init() {
		networkTop = new NetworkTopology();

		LNVertex v0 = networkTop.addNode(0, 1, new LNVertex.NetworkStatus(1));
		LNVertex v1 = networkTop.addNode(1, 0, new LNVertex.NetworkStatus(1));
		LNVertex v2 = networkTop.addNode(2, 0, new LNVertex.NetworkStatus(1));
		LNVertex v3 = networkTop.addNode(3, 0, new LNVertex.NetworkStatus(1));
		LNVertex v4 = networkTop.addNode(4, 0, new LNVertex.NetworkStatus(1));

		networkTop.addChannel(v0, v1, LNEdge.ChannelStatus.OPENED, 3, 0);
		networkTop.addChannel(v0, v2, LNEdge.ChannelStatus.OPENED, 4, 0);
		networkTop.addChannel(v0, v3, LNEdge.ChannelStatus.OPENED, 5, 0);

		networkTop.addChannel(v1, v2, LNEdge.ChannelStatus.OPENED, 2, 0);

		networkTop.addChannel(v2, v3, LNEdge.ChannelStatus.OPENED, 4, 0);
		networkTop.addChannel(v2, v4, LNEdge.ChannelStatus.OPENED, 1, 0);

		networkTop.addChannel(v3, v4, LNEdge.ChannelStatus.OPENED, 10, 0);
	}

	@Test
	public void testMultiPath() {
		SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph = networkTop.getNetworkGraph();

		int[][] capacity = new int[noNodes][noNodes];
		int[][] fee = new int[noNodes][noNodes];

		for (int i = 0; i < noNodes; i++) {
			for (int j = 0; j < i; j++) {
				Set<LNEdge> edgeSet = networkGraph.getAllEdges(new LNVertex(i), new LNVertex(j));
				for (LNEdge e : edgeSet) {
					capacity[i][j] = e.tokenAmount;
					fee[i][j] = (int) e.getWeight();// TODO: Change cast
				}

				Set<LNEdge> edgeSet2 = networkGraph.getAllEdges(new LNVertex(j), new LNVertex(i));
				for (LNEdge e : edgeSet2) {
					capacity[j][i] = e.tokenAmount;
					fee[j][i] = (int) e.getWeight();// TODO: Change cast
				}
			}
		}

		int[] ret = flowAlg.getMaxFlow(capacity, fee, 0, 4);
		assertEquals("Max flow", true, ret[0] == 10);
		assertEquals("Min cost", true, ret[1] == 10);
	}
}
