package io.gridplus.ln.simulation.model;

import java.util.Collections;
import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.KShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import io.gridplus.ln.simulation.model.LNVertex.NetworkStatus;
import io.gridplus.ln.simulation.network.LNPathComparator;
import io.gridplus.ln.simulation.network.LNPathValidator;

public class NetworkTopology {

	private SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph;

	public NetworkTopology() {
		networkGraph = new SimpleDirectedWeightedGraph<LNVertex, LNEdge>(LNEdge.class);
	}

	public LNVertex addNode(int id, double fee, NetworkStatus status) {
		LNVertex vertex = new LNVertex(id, fee);
		vertex.networkStatus = new NetworkStatus(1);
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
