package io.gridplus.ln.network.topology;

import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.PathValidator;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import io.gridplus.ln.model.LNEdge;
import io.gridplus.ln.model.LNVertex;

public interface KShortestPathsAlgorithmInterface {
	List<GraphPath<LNVertex, LNEdge>> findShortestPaths(SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph,
			LNVertex id1, LNVertex id2, PathValidator<LNVertex, LNEdge> validator);
}
