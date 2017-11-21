package io.gridplus.ln.network.topology;

import java.util.Collections;
import java.util.List;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.KShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.alg.shortestpath.PathValidator;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import io.gridplus.ln.model.LNEdge;
import io.gridplus.ln.model.LNPathComparator;
import io.gridplus.ln.model.LNVertex;

public class JGraphtKShortestPaths implements KShortestPathsAlgorithmInterface {

	@Override
	public List<GraphPath<LNVertex, LNEdge>> findShortestPaths(SimpleDirectedWeightedGraph<LNVertex, LNEdge> networkGraph, LNVertex id1, LNVertex id2,
			PathValidator<LNVertex, LNEdge> validator) {
		KShortestPathAlgorithm<LNVertex, LNEdge> pathsAlg = new KShortestPaths<LNVertex, LNEdge>(networkGraph, 3,
				validator);
		List<GraphPath<LNVertex, LNEdge>> paths = pathsAlg.getPaths(id1, id2);
		Collections.sort(paths, new LNPathComparator());
		return paths;
	}

}
