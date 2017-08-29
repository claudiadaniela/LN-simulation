package io.gridplus.ln.model;

import java.util.Comparator;

import org.jgrapht.GraphPath;

public class LNPathComparator implements Comparator<GraphPath<LNVertex, LNEdge>> {

    public int compare(GraphPath<LNVertex, LNEdge> o1, GraphPath<LNVertex, LNEdge> o2) {
        double compWeight = o1.getWeight() - o2.getWeight();
        if (compWeight != 0) {
            return (int) compWeight;
        }
        return o1.getLength() - o2.getLength();
    }

}
