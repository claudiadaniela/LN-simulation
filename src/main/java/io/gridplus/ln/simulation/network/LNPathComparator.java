package io.gridplus.ln.simulation.network;

import java.util.Comparator;

import org.jgrapht.GraphPath;

import io.gridplus.ln.simulation.model.LNEdge;
import io.gridplus.ln.simulation.model.LNVertex;

public class LNPathComparator implements Comparator<GraphPath<LNVertex, LNEdge>> {

	public int compare(GraphPath<LNVertex, LNEdge> o1, GraphPath<LNVertex, LNEdge> o2) {
		double compWeight = o1.getWeight() - o2.getWeight();
		if (compWeight != 0) {
			return (int) compWeight;
		}
		return o1.getLength()- o2.getLength();
	}

}
