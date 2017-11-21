package io.gridplus.ln.network.topology;

import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.PathValidator;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import io.gridplus.ln.model.LNEdge;
import io.gridplus.ln.model.LNVertex;
import io.gridplus.ln.multipath.MinCostMaxFlowAlgorithm;

public class MultiKShortestPathsAlgorithm implements KShortestPathsAlgorithmInterface {

	@Override
	public List<GraphPath<LNVertex, LNEdge>> findShortestPaths(
			SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph, LNVertex id1, LNVertex id2,
			PathValidator<LNVertex, LNEdge> validator) {
		
		MinCostMaxFlowAlgorithm alg = MinCostMaxFlowAlgorithm.getInstance(networkGraph);
		double[] ret = alg.getMaxFlow(id1.getId(), id2.getId());
		
		
		return null;
	}

}
