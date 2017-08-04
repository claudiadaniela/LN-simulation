package io.gridplus.ln.simulation.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.KShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import io.gridplus.ln.simulation.model.LNEdge;
import io.gridplus.ln.simulation.model.LNVertex;
import io.gridplus.ln.simulation.model.LNVertex.NetworkStatus;

public class NetworkTopology {

	private SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph;
	private List<LNVertex> hops;
	private static final int V_TOKEN = 100;

	public NetworkTopology() {
		networkGraph = new SimpleDirectedWeightedGraph<LNVertex, LNEdge>(LNEdge.class);
	}

	public SimpleDirectedWeightedGraph<LNVertex, LNEdge> createNetworkGraph(int noHops, int size) {
		Random rand = new Random();
		hops = new ArrayList<LNVertex>();
		for (int i = 0; i < noHops; i++) {
			LNVertex hop = addNode(i, rand.nextDouble(), new LNVertex.NetworkStatus(1));
			hops.add(hop);
			if (i - 1 >= 0) {
				LNVertex hop0 = hops.get(i - 1);
				int tokenAmountV1 = rand.nextInt(100) + V_TOKEN * size / noHops;
				int tokenAmountV2 = rand.nextInt(100) + V_TOKEN * size / noHops;
				addChannel(hop0, hop, LNEdge.ChannelStatus.OPENED, tokenAmountV1, tokenAmountV2);
				System.out.println("Hop Channel: " + hop0 + "-" + hop);
			}
		}

		for (int i = noHops; i < size + noHops; i++) {
			LNVertex v1 = addNode(i, rand.nextDouble(), new LNVertex.NetworkStatus(1));
			LNVertex v2 = hops.get(rand.nextInt(noHops));

			int tokenAmountV1 = rand.nextInt(50) + V_TOKEN;
			int tokenAmountV2 = rand.nextInt(50) + V_TOKEN;
			addChannel(v1, v2, LNEdge.ChannelStatus.OPENED, tokenAmountV1, tokenAmountV2);
			System.out.println("Channel: " + v1 + "-" + v2);
		}
		return networkGraph;
	}

	public LNVertex addNode(int id, double fee, NetworkStatus status) {
		LNVertex vertex = new LNVertex(id, fee);
		vertex.networkStatus = new LNVertex.NetworkStatus(1);
		networkGraph.addVertex(vertex);
		return vertex;
	}

	public void addChannel(LNVertex v1, LNVertex v2, LNEdge.ChannelStatus status, int tokenAmountV1,
			int tokenAmountV2) {
		LNEdge e12 = networkGraph.addEdge(v1, v2);
		e12.status = status;
		e12.tokenAmount = tokenAmountV1;
		networkGraph.setEdgeWeight(e12, v1.getFee());
		LNEdge e21 = networkGraph.addEdge(v2, v1);
		e21.status = status;
		e21.tokenAmount = tokenAmountV2;
		networkGraph.setEdgeWeight(e21, v2.getFee());
	}

	public List<GraphPath<LNVertex, LNEdge>> computeShortestPaths(int id1, int id2, int amount) {
		KShortestPathAlgorithm<LNVertex, LNEdge> pathsAlg = new KShortestPaths<LNVertex, LNEdge>(networkGraph, 10,
				new LNPathValidator(amount));
		List<GraphPath<LNVertex, LNEdge>> paths = pathsAlg.getPaths(new LNVertex(id1), new LNVertex(id2));
		Collections.sort(paths, new LNPathComparator());
		return paths;

	}

	public SimpleDirectedWeightedGraph<LNVertex, LNEdge> getNetworkGraph() {
		return networkGraph;
	}
}
